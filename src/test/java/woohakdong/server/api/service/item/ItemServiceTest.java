package woohakdong.server.api.service.item;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;
import static woohakdong.server.common.exception.CustomErrorInfo.ITEM_NOT_AVAILABLE;
import static woohakdong.server.common.exception.CustomErrorInfo.ITEM_USING;
import static woohakdong.server.domain.clubmember.ClubMemberRole.MEMBER;
import static woohakdong.server.domain.item.ItemCategory.SPORT;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import woohakdong.server.api.controller.ListWrapperResponse;
import woohakdong.server.api.controller.item.dto.*;
import woohakdong.server.common.exception.CustomException;
import woohakdong.server.common.security.jwt.CustomUserDetails;
import woohakdong.server.domain.ItemHistory.ItemHistory;
import woohakdong.server.domain.ItemHistory.ItemHistoryRepository;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.club.ClubRepository;
import woohakdong.server.domain.clubmember.ClubMember;
import woohakdong.server.domain.clubmember.ClubMemberRepository;
import woohakdong.server.domain.clubmember.ClubMemberRole;
import woohakdong.server.domain.item.Item;
import woohakdong.server.domain.item.ItemCategory;
import woohakdong.server.domain.item.ItemRepository;
import woohakdong.server.domain.itemBorrowed.ItemBorrowed;
import woohakdong.server.domain.itemBorrowed.ItemBorrowedRepository;
import woohakdong.server.domain.member.Member;
import woohakdong.server.domain.member.MemberRepository;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class ItemServiceTest {
    @Autowired
    private ItemService itemService;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ItemHistoryRepository itemHistoryRepository;

    @Autowired
    private ClubMemberRepository clubMemberRepository;

    @Autowired
    private ItemBorrowedRepository itemBorrowedRepository;

    @DisplayName("물품을 등록하면 물품을 확인할 수 있다.")
    @Test
    void registerItem() {
        // Given
        Club club = createClub();
        ItemRegisterRequest request = createItemRegisterRequest("축구공", SPORT, 7);

        // When
        ItemRegisterResponse response = itemService.registerItem(club.getClubId(), request);

        // Then
        assertThat(response)
                .extracting("itemId")
                .isNotNull();

        Item savedItem = itemRepository.getById(response.itemId());
        assertThat(savedItem)
                .extracting("itemName", "itemCategory", "itemRentalMaxDay")
                .containsExactly("축구공", SPORT, 7);
    }


    @DisplayName("물품리스트를 확인할 수 있다.")
    @Test
    void getItemsByClubId() {
        // Given
        Club club = createClub();
        createItem(club, "축구공", SPORT, 7, false);
        createItem(club, "농구공", SPORT, 7, false);

        // When
        List<ItemResponse> items = itemService.getItemsByFilters(club.getClubId(), null, null);

        // Then
        assertThat(items).hasSize(2)
                .extracting("itemName", "itemCategory")
                .containsExactlyInAnyOrder(
                        tuple("축구공", SPORT),
                        tuple("농구공", SPORT)
                );
    }

    @DisplayName("물품 대여를 성공적으로 할 수 있다.")
    @Test
    void borrowItemSuccess() {
        // given
        Member member = setUpMemberSession();
        Club club = createClub();
        Item item = createItem(club, "축구공", SPORT, 7, false);
        ClubMember clubMember = createClubMember(club, member, MEMBER, getAssignedTerm(LocalDate.now()));

        // when
        itemService.borrowItem(club.getClubId(), item.getItemId());

        // then
        Item borrowedItem = itemRepository.getById(item.getItemId());
        assertThat(borrowedItem)
                .extracting("itemUsing", "itemAvailable")
                .containsExactly(true, true);

        assertThat(borrowedItem.getItemRentalDate()).isBefore(LocalDateTime.now());
    }

    @DisplayName("물품이 대여 불가능한 상태라면, 대여할 수 없다.")
    @Test
    void borrowItem() {
        // Given
        Member member = setUpMemberSession();
        Club club = createClub();
        Item item = createItem(club, "축구공", SPORT, 7, false);
        item.setItemAvailable(false);
        ClubMember clubMember = createClubMember(club, member, MEMBER, getAssignedTerm(LocalDate.now()));

        // When & Then
        assertThatThrownBy(() -> itemService.borrowItem(club.getClubId(), item.getItemId()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ITEM_NOT_AVAILABLE.getMessage());
    }

    @DisplayName("이미 대여된 물품은 대여를 할 수 없다.")
    @Test
    void borrowItemAlreadyInUseFailure() {
        // given
        Member member = setUpMemberSession();
        Club club = createClub();
        Item item = createItem(club, "축구공", SPORT, 7, true);
        ClubMember clubMember = createClubMember(club, member, MEMBER, getAssignedTerm(LocalDate.now()));

        // when & then
        assertThatThrownBy(() -> itemService.borrowItem(club.getClubId(), item.getItemId()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ITEM_USING.getMessage());
    }

    @DisplayName("물품을 성공적으로 반납할 수 있다.")
    @Test
    void returnItemSuccess() {
        // given
        Member member = setUpMemberSession();
        Club club = createClub();
        Item item = createItem(club, "축구공", SPORT, 7, false);
        ItemReturnRequest request = createItemReturnRequest("http://example.com/return_photo.png");
        ClubMember clubMember = createClubMember(club, member, MEMBER, getAssignedTerm(LocalDate.now()));

        itemService.borrowItem(club.getClubId(), item.getItemId());

        // when
        ItemReturnResponse itemReturnResponse = itemService.returnItem(club.getClubId(), item.getItemId(), request);

        // then
        Item returnedItem = itemRepository.getById(item.getItemId());
        assertThat(returnedItem)
                .extracting("itemUsing", "itemAvailable")
                .containsExactly(false, true);

        // 반납 기록이 잘 저장되었는지 확인
        ItemHistory history = itemHistoryRepository.getById(itemReturnResponse.itemHistoryId());
        assertThat(history.getItemReturnDate()).isBefore(LocalDateTime.now());
    }

    @DisplayName("물품대여 기록을 조회할 수 있다.")
    @Test
    void getItemHistorySuccess() {
        // given
        Member member = setUpMemberSession();
        Club club = createClub();
        Item item = createItem(club, "축구공", SPORT, 7, false);

        LocalDateTime now = LocalDateTime.now();
        createItemHistory(item, member, now.minusDays(10), now.minusDays(3), now.minusDays(2));
        createItemHistory(item, member, now, now.plusDays(7), null);

        // when
        List<ItemHistoryResponse> itemHistoryResponses = itemService.getItemHistory(club.getClubId(), item.getItemId());

        // then
        assertThat(itemHistoryResponses)
                .extracting("itemRentalDate", "itemDueDate", "itemReturnDate")
                .containsExactly(
                        tuple(now.minusDays(10), now.minusDays(3), now.minusDays(2)),
                        tuple(now, now.plusDays(7), null)
                );
    }

    @DisplayName("물품이름으로 검색할 수 있다.")
    @Test
    void searchItemsByName_success() {
        // given
        setUpMemberSession();

        Club club = createClub();
        createItem(club, "축구공", SPORT, 7, false);
        createItem(club, "농구공", SPORT, 5, false);

        // when
        List<ItemResponse> items = itemService.getItemsByFilters(club.getClubId(), "공", "");

        // then
        assertThat(items).hasSize(2)
                .extracting("itemName", "itemCategory", "itemRentalMaxDay")
                .containsExactlyInAnyOrder(
                        tuple("축구공", SPORT, 7),
                        tuple("농구공", SPORT, 5)
                );
    }

    @DisplayName("물품 이용 가능을 수정할 수 있다.")
    @Test
    void updateItemAvailability_success() {
        // given
        setUpMemberSession();

        Club club = createClub();
        Item item = createItem(club, "축구공", SPORT, 7, false);
        ItemAvailableUpdateRequest request = createAvailableRequest(false);

        // when
        itemService.updateItemAvailability(club.getClubId(), item.getItemId(), request);

        // Then
        Item updatedItem = itemRepository.getById(item.getItemId());
        assertThat(updatedItem.getItemAvailable()).isFalse();
    }

    @DisplayName("물품을 수정할 수 있다.")
    @Test
    void updateItem_success() {
        // given
        setUpMemberSession();

        Club club = createClub();
        Item item = createItem(club, "축구공", SPORT, 7, false);
        ItemUpdateRequest updateRequest = createUpdateRequests("새로운 축구공", SPORT, 9);

        // When
        itemService.updateItem(club.getClubId(), item.getItemId(), updateRequest);

        // Then
        Item updatedItem = itemRepository.getById(item.getItemId());
        assertThat(updatedItem)
                .extracting("itemName", "itemRentalMaxDay")
                .containsExactly("새로운 축구공", 9);
    }

    @DisplayName("빌린 물품 리스트를 확인할 수 있다.")
    @Test
    void getMyBorrowedItems_success() {
        // given
        Member member = setUpMemberSession();
        Club club = createClub();
        Item item = createItem(club, "축구공", SPORT, 7, false);
        ClubMember clubMember = createClubMember(club, member, MEMBER, getAssignedTerm(LocalDate.now()));
        ItemBorrowed itemBorrowed = createItemBorrowed(clubMember, item);

        // when
        ListWrapperResponse<ItemBorrowedResponse> response = itemService.getMyBorrowedItems(club.getClubId());

        // then
        assertThat(response.result())
                .isNotEmpty()
                .extracting("itemName")
                .containsExactly("축구공");
    }

    @DisplayName("물품 상세 정보를 확인할 수 있다.")
    @Test
    void getItemInfo_success() {
        // given
        Club club = createClub();
        Item item = createItem(club, "축구공", SPORT, 7, false);

        // when
        ItemResponse response = itemService.getItemInfo(club.getClubId(), item.getItemId());

        // then
        assertThat(response)
                .extracting("itemName", "itemCategory", "itemPhoto")
                .containsExactly("축구공", ItemCategory.SPORT, "https://item-image.com");
    }

    @DisplayName("자신의 물품 대여 기록 리스트를 확인할 수 있다.")
    @Test
    void getMyHistoryItems_success() {
        // given
        Member member = setUpMemberSession();
        Club club = createClub();
        Item item = createItem(club, "축구공", SPORT, 7, false);
        ClubMember clubMember = createClubMember(club, member, MEMBER, getAssignedTerm(LocalDate.now()));

        LocalDateTime now = LocalDateTime.of(2024, 11, 8, 2, 17, 4, 844856);
        ItemHistory itemHistory = createItemHistory(item, member, now.minusDays(10), now.minusDays(3), now.minusDays(2));

        // when
        ListWrapperResponse<ItemHistoryResponse> response = itemService.getMyHistoryItems(club.getClubId());

        // then
        assertThat(response.result())
                .isNotEmpty()
                .extracting("itemRentalDate", "itemDueDate")
                .containsExactly(tuple(now.minusDays(10), now.minusDays(3)));
    }

    @DisplayName("동아리 회원의 물품 대여 기록 리스트를 확인할 수 있다.")
    @Test
    void getClubMemberHistoryItems_success() {
        // given
        Member member = setUpMemberSession();
        Club club = createClub();
        Item item = createItem(club, "축구공", SPORT, 7, false);
        ClubMember clubMember = createClubMember(club, member, MEMBER, getAssignedTerm(LocalDate.now()));

        LocalDateTime now = LocalDateTime.of(2024, 11, 8, 2, 17, 4, 844856);
        ItemHistory itemHistory = createItemHistory(item, member, now.minusDays(10), now.minusDays(3), now.minusDays(2));

        // when
        ListWrapperResponse<ItemHistoryResponse> response = itemService.getClubMemberHistoryItems(club.getClubId(), clubMember.getClubMemberId());

        // then
        assertThat(response.result())
                .isNotEmpty()
                .extracting("itemRentalDate", "itemDueDate")
                .containsExactly(tuple(now.minusDays(10), now.minusDays(3)));
    }

    private ItemBorrowed createItemBorrowed(ClubMember clubMember, Item item) {
        ItemBorrowed itemBorrowed = ItemBorrowed.builder()
                .itemBorrowedReturnDate(LocalDateTime.now().plusDays(7))
                .clubMember(clubMember)
                .item(item)
                .build();
        itemBorrowedRepository.save(itemBorrowed);
        return itemBorrowed;
    }

    private Member setUpMemberSession() {
        String provideId = "testProvideId";
        Member member = Member.builder()
                .memberProvideId(provideId)
                .memberName("John Doe")
                .memberEmail("john.doe@example.com")
                .build();
        memberRepository.save(member);

        String role = "USER_ROLE";
        CustomUserDetails customUserDetails = new CustomUserDetails(provideId, role);
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        return member;
    }

    private Club createClub() {
        Club club = Club.builder()
                .clubName("테스트 동아리")
                .clubEnglishName("testClub")
                .clubGroupChatLink("https://club-group-chat-link.com")
                .build();
        return clubRepository.save(club);
    }

    private Item createItem(Club club, String itemName, ItemCategory itemCategory, int day, boolean using) {
        Item item = Item.builder()
                .itemName(itemName)
                .itemPhoto("https://item-image.com")
                .itemAvailable(true)
                .itemUsing(using)
                .itemCategory(itemCategory)
                .itemRentalMaxDay(day)
                .itemRentalTime(0)
                .club(club)
                .build();
        return itemRepository.save(item);
    }

    private ItemReturnRequest createItemReturnRequest(String returnImage) {
        return new ItemReturnRequest(returnImage);
    }

    private ItemAvailableUpdateRequest createAvailableRequest(boolean available) {
        return new ItemAvailableUpdateRequest(available);
    }

    private ItemUpdateRequest createUpdateRequests(String itemName, ItemCategory itemCategory, int rentalMaxDay) {
        return ItemUpdateRequest.builder()
                .itemName(itemName)
                .itemPhoto("http://example.com/new_soccer_ball.png")
                .itemDescription("Updated soccer ball description")
                .itemLocation("Updated Location")
                .itemCategory(itemCategory)
                .itemRentalMaxDay(rentalMaxDay)
                .build();
    }

    private ItemRegisterRequest createItemRegisterRequest(String name, ItemCategory category, int rentalMaxDay) {
        return ItemRegisterRequest.builder()
                .itemName(name)
                .itemPhoto("http://example-item-photo.com")
                .itemDescription("Item Description")
                .itemLocation("Club Storage Room")
                .itemCategory(category)
                .itemRentalMaxDay(rentalMaxDay)
                .build();
    }

    private ItemHistory createItemHistory(Item item, Member member, LocalDateTime rentalDate, LocalDateTime dueDate,
                                   LocalDateTime returnDate) {
        ItemHistory itemHistory = ItemHistory.builder()
                .item(item)
                .member(member)
                .itemRentalDate(rentalDate) // 10일 전 대여
                .itemDueDate(dueDate)     // 3일 전에 반납 예정
                .itemReturnDate(returnDate)  // 2일 전에 반납됨
                .itemReturnImage("http://example.com/return_photo.png")
                .build();
        itemHistoryRepository.save(itemHistory);
        return itemHistory;
    }

    private ClubMember createClubMember(Club club, Member member, ClubMemberRole memberRole, LocalDate assignedTerm) {
        ClubMember clubMember = ClubMember.builder()
                .clubMemberAssignedTerm(getAssignedTerm(assignedTerm))
                .club(club)
                .member(member)
                .clubMemberRole(memberRole)
                .build();
        return clubMemberRepository.save(clubMember);
    }

    private LocalDate getAssignedTerm(LocalDate date) {
        int year = date.getYear();
        int semester = date.getMonthValue() <= 6 ? 1 : 7; // 1: 1학기, 7: 2학기
        return LocalDate.of(year, semester, 1);
    }
}