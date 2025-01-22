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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import woohakdong.server.api.controller.SliceResponse;
import woohakdong.server.api.controller.item.dto.ItemAvailableUpdateRequest;
import woohakdong.server.api.controller.item.dto.ItemBorrowedResponse;
import woohakdong.server.api.controller.item.dto.ItemHistoryResponse;
import woohakdong.server.api.controller.item.dto.ItemInfoResponse;
import woohakdong.server.api.controller.item.dto.ItemRegisterRequest;
import woohakdong.server.api.controller.item.dto.ItemRegisterResponse;
import woohakdong.server.api.controller.item.dto.ItemResponse;
import woohakdong.server.api.controller.item.dto.ItemReturnRequest;
import woohakdong.server.api.controller.item.dto.ItemReturnResponse;
import woohakdong.server.api.controller.item.dto.ItemUpdateRequest;
import woohakdong.server.SecurityContextSetup;
import woohakdong.server.common.exception.CustomException;
import woohakdong.server.common.util.date.DateUtil;
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

class ItemServiceTest extends SecurityContextSetup {
    @Autowired
    private ItemService itemService;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemHistoryRepository itemHistoryRepository;

    @Autowired
    private ClubMemberRepository clubMemberRepository;

    @Autowired
    private ItemBorrowedRepository itemBorrowedRepository;

    @Autowired
    private DateUtil dateUtil;

    @BeforeEach
    void setUp() {
        club = createClub();
        member = createExampleMember();
    }

    private Club club;
    private Member member;

    @DisplayName("물품을 등록하면 물품을 확인할 수 있다.")
    @Test
    void registerItem() {
        // Given
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
        createItem(club, "축구공", SPORT, 7, false);
        createItem(club, "농구공", SPORT, 7, false);

        Pageable pageable = PageRequest.of(0, 10);

        // When
        SliceResponse<ItemResponse> items = itemService.getItemsByFilters(club.getClubId(), null, null, null, null, null, pageable);

        // Then
        assertThat(items.result()).hasSize(2)
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
        Item item = createItem(club, "축구공", SPORT, 7, false);
        LocalDate date = LocalDate.now();
        ClubMember clubMember = createClubMember(club, member, MEMBER, date);

        // when
        itemService.borrowItem(club.getClubId(), item.getItemId(), date);

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
        Item item = createItem(club, "축구공", SPORT, 7, false);
        item.setItemAvailable(false);
        LocalDate date = LocalDate.now();
        ClubMember clubMember = createClubMember(club, member, MEMBER, date);

        // When & Then
        assertThatThrownBy(() -> itemService.borrowItem(club.getClubId(), item.getItemId(), date))
                .isInstanceOf(CustomException.class)
                .hasMessage(ITEM_NOT_AVAILABLE.getMessage());
    }

    @DisplayName("이미 대여된 물품은 대여를 할 수 없다.")
    @Test
    void borrowItemAlreadyInUseFailure() {
        // given
        Item item = createItem(club, "축구공", SPORT, 7, true);
        LocalDate date = LocalDate.now();
        ClubMember clubMember = createClubMember(club, member, MEMBER, date);

        // when & then
        assertThatThrownBy(() -> itemService.borrowItem(club.getClubId(), item.getItemId(), date))
                .isInstanceOf(CustomException.class)
                .hasMessage(ITEM_USING.getMessage());
    }

    @DisplayName("물품을 성공적으로 반납할 수 있다.")
    @Test
    void returnItemSuccess() {
        // given
        Item item = createItem(club, "축구공", SPORT, 7, false);
        ItemReturnRequest request = createItemReturnRequest("http://example.com/return_photo.png");
        LocalDate date = LocalDate.now();
        createClubMember(club, member, MEMBER, date);
        itemService.borrowItem(club.getClubId(), item.getItemId(), date);

        // when
        ItemReturnResponse response = itemService.returnItem(club.getClubId(), item.getItemId(), request, date);

        // then
        Item returnedItem = itemRepository.getById(item.getItemId());
        assertThat(returnedItem)
                .extracting("itemUsing", "itemAvailable")
                .containsExactly(false, true);

        // 반납 기록이 잘 저장되었는지 확인
        ItemHistory history = itemHistoryRepository.getById(response.itemHistoryId());
        assertThat(history.getItemReturnDate()).isBefore(LocalDateTime.now());
    }

    @DisplayName("물품대여 기록을 조회할 수 있다.")
    @Test
    void getItemHistorySuccess() {
        // given
        Item item = createItem(club, "축구공", SPORT, 7, false);
        LocalDate date = LocalDate.now();
        ClubMember clubMember = createClubMember(club, member, MEMBER, date);

        LocalDateTime dateTime = date.atStartOfDay();
        createItemHistory(item, clubMember, dateTime.minusDays(10), dateTime.minusDays(3), dateTime.minusDays(2), club);
        createItemHistory(item, clubMember, dateTime, dateTime.plusDays(7), null, club);

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Slice<ItemHistoryResponse> itemHistoryResponses = itemService.getItemHistory(club.getClubId(), item.getItemId(), pageable);

        // then
        assertThat(itemHistoryResponses)
                .extracting("itemRentalDate", "itemDueDate", "itemReturnDate")
                .containsExactly(
                        tuple(dateTime, dateTime.plusDays(7), null),
                        tuple(dateTime.minusDays(10), dateTime.minusDays(3), dateTime.minusDays(2))
                );
    }

    @DisplayName("동아리별 물품대여 기록을 조회할 수 있다.")
    @Test
    void getClubItemHistorySuccess() {
        // given
        Item item = createItem(club, "축구공", SPORT, 7, false);
        LocalDate date = LocalDate.now();
        ClubMember clubMember = createClubMember(club, member, MEMBER, date);

        LocalDateTime dateTime = date.atStartOfDay();
        createItemHistory(item, clubMember, dateTime.minusDays(10), dateTime.minusDays(3), dateTime.minusDays(2), club);
        createItemHistory(item, clubMember, dateTime, dateTime.plusDays(7), null, club);

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Slice<ItemHistoryResponse> itemHistoryResponses = itemService.getAllItemHistory(club.getClubId(), pageable);

        // then
        assertThat(itemHistoryResponses.getContent())
                .extracting("itemRentalDate", "itemDueDate", "itemReturnDate")
                .containsExactly(
                        tuple(dateTime, dateTime.plusDays(7), null),
                        tuple(dateTime.minusDays(10), dateTime.minusDays(3), dateTime.minusDays(2))
                );
    }

    @DisplayName("물품이름으로 검색할 수 있다.")
    @Test
    void searchItemsByName_success() {
        // given
        createItem(club, "축구공", SPORT, 7, false);
        createItem(club, "농구공", SPORT, 5, false);

        Pageable pageable = PageRequest.of(0, 10);

        // when
        SliceResponse<ItemResponse> items = itemService.getItemsByFilters(club.getClubId(), "공", "", false, true, null, pageable);

        // then
        assertThat(items.result()).hasSize(2)
                .extracting("itemName", "itemCategory", "itemRentalMaxDay")
                .containsExactlyInAnyOrder(
                        tuple("축구공", SPORT, 7),
                        tuple("농구공", SPORT, 5)
                );
    }

    @DisplayName("사용중인 물품을 검색할 수 있다.")
    @Test
    void searchItemsByUsing_success() {
        // given
        createItem(club, "축구공", SPORT, 7, false);
        Item item = createItem(club, "농구공", SPORT, 5, true);
        LocalDate date = LocalDate.now();
        ClubMember clubMember = createClubMember(club, member, MEMBER, date);
        LocalDateTime dateTime = date.atStartOfDay();

        ItemHistory itemHistory = createItemHistory(item, clubMember, dateTime.minusDays(10), dateTime.minusDays(3),
                null, club);

        Pageable pageable = PageRequest.of(0, 10);

        // when
        SliceResponse<ItemResponse> items = itemService.getItemsByFilters(club.getClubId(), "공", "", true, true, null, pageable);

        // then
        assertThat(items.result())
                .hasSize(1)
                .extracting("itemName", "itemCategory", "itemRentalMaxDay", "itemUsing")
                .containsExactly(tuple("농구공", ItemCategory.SPORT, 5, true));

    }

    @DisplayName("연체된 물품을 검색할 수 있다.")
    @Test
    void searchItemsByOverdue_success() {
        // given
        createItem(club, "축구공", SPORT, 7, false);
        Item item = createItem(club, "농구공", SPORT, 5, true);
        LocalDate date = LocalDate.now();
        ClubMember clubMember = createClubMember(club, member, MEMBER, date);
        LocalDateTime dateTime = date.atStartOfDay();
        createItemHistory(item, clubMember, dateTime.minusDays(10), dateTime.minusDays(3), null, club);

        Pageable pageable = PageRequest.of(0, 10);

        // when
        SliceResponse<ItemResponse> items = itemService.getItemsByFilters(club.getClubId(), "공", "", true, true, true, pageable);

        // then
        assertThat(items.result())
                .hasSize(1)
                .extracting("itemName", "itemCategory", "itemRentalMaxDay", "itemUsing")
                .containsExactly(tuple("농구공", ItemCategory.SPORT, 5, true));

    }

    @DisplayName("연체되지 않은 물품을 검색할 수 있다.")
    @Test
    void searchItemsByNotOverdue_success() {
        // given
        createItem(club, "축구공", SPORT, 7, false);
        Item item = createItem(club, "농구공", SPORT, 5, true);
        LocalDate date = LocalDate.now();
        ClubMember clubMember = createClubMember(club, member, MEMBER, date);

        LocalDateTime dateTime = date.atStartOfDay();
        createItemHistory(item, clubMember, dateTime.minusDays(3), dateTime.plusDays(4), null, club);

        Pageable pageable = PageRequest.of(0, 10);

        // when
        SliceResponse<ItemResponse> items = itemService.getItemsByFilters(club.getClubId(), "공", "", null, null, false, pageable);

        // then
        assertThat(items.result())
                .hasSize(2);
    }

    @DisplayName("물품 이용 가능을 수정할 수 있다.")
    @Test
    void updateItemAvailability_success() {
        // given
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
        Item item = createItem(club, "축구공", SPORT, 7, false);
        LocalDate date = LocalDate.now();
        ClubMember clubMember = createClubMember(club, member, MEMBER, date);
        ItemBorrowed itemBorrowed = createItemBorrowed(clubMember, item);

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Slice<ItemBorrowedResponse> response = itemService.getMyBorrowedItems(club.getClubId(), date, pageable);

        // then
        assertThat(response.getContent())
                .isNotEmpty()
                .extracting("itemName")
                .containsExactly("축구공");
    }

    @DisplayName("물품 상세 정보를 확인할 수 있다.")
    @Test
    void getItemInfo_success() {
        // given
        Item item = createItem(club, "축구공", SPORT, 7, false);

        // when
        ItemInfoResponse response = itemService.getItemInfo(club.getClubId(), item.getItemId());

        // then
        assertThat(response)
                .extracting("itemName", "itemCategory", "itemPhoto")
                .containsExactly("축구공", ItemCategory.SPORT, "https://item-image.com");
    }

    @DisplayName("자신의 물품 대여 기록 리스트를 확인할 수 있다.")
    @Test
    void getMyHistoryItems_success() {
        // given
        Item item = createItem(club, "축구공", SPORT, 7, false);
        LocalDate date = LocalDate.now();
        ClubMember clubMember = createClubMember(club, member, MEMBER, date);

        LocalDateTime dateTime = date.atStartOfDay();
        ItemHistory itemHistory = createItemHistory(item, clubMember, dateTime.minusDays(10), dateTime.minusDays(3),
                dateTime.minusDays(2), club);

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Slice<ItemHistoryResponse> response = itemService.getMyHistoryItems(club.getClubId(), date, pageable);

        // then
        assertThat(response.getContent())
                .isNotEmpty()
                .extracting("itemRentalDate", "itemDueDate")
                .containsExactly(tuple(dateTime.minusDays(10), dateTime.minusDays(3)));
    }

    @DisplayName("동아리 회원의 물품 대여 기록 리스트를 확인할 수 있다.")
    @Test
    void getClubMemberHistoryItems_success() {
        // given
        Item item = createItem(club, "축구공", SPORT, 7, false);
        LocalDate now = LocalDate.now();
        ClubMember clubMember = createClubMember(club, member, MEMBER, now);

        LocalDateTime dateTime = now.atStartOfDay();
        createItemHistory(item, clubMember, dateTime.minusDays(10), dateTime.minusDays(3), null, club);

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Slice<ItemHistoryResponse> response = itemService.getClubMemberHistoryItems(club.getClubId(),
                clubMember.getClubMemberId(), pageable);

        // then
        assertThat(response.getContent())
                .isNotEmpty()
                .extracting("itemRentalDate", "itemDueDate", "itemName", "itemOverdue")
                .containsExactly(tuple(dateTime.minusDays(10), dateTime.minusDays(3), "축구공", true));
    }

    @DisplayName("동아리 회원의 물품 대여 기록 연체된 것을 확인할 수 있다.")
    @Test
    void getClubMemberHistoryItemsOverdue_success() {
        // given
        Item item = createItem(club, "축구공", SPORT, 7, false);
        LocalDate date = LocalDate.now();
        ClubMember clubMember = createClubMember(club, member, MEMBER, date);

        LocalDateTime dateTime = date.atStartOfDay();
        createItemHistory(item, clubMember, dateTime.minusDays(10), dateTime.minusDays(3), dateTime.minusDays(2), club);

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Slice<ItemHistoryResponse> response = itemService.getClubMemberHistoryItems(club.getClubId(),
                clubMember.getClubMemberId(), pageable);

        // then
        assertThat(response.getContent())
                .isNotEmpty()
                .extracting("itemRentalDate", "itemDueDate", "itemName", "itemOverdue")
                .containsExactly(tuple(dateTime.minusDays(10), dateTime.minusDays(3), "축구공", true));
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

    private Club createClub() {
        Club club = Club.builder()
                .clubName("테스트 동아리")
                .clubEnglishName("testClub")
                .clubGroupChatLink("https://club-group-chat-link.com")
                .clubExpirationDate(LocalDate.of(2024, 11, 19))
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

    private ItemHistory createItemHistory(Item item, ClubMember clubMember, LocalDateTime rentalDate,
                                          LocalDateTime dueDate,
                                          LocalDateTime returnDate, Club club) {
        ItemHistory itemHistory = ItemHistory.builder()
                .item(item)
                .clubMember(clubMember)
                .itemRentalDate(rentalDate) // 10일 전 대여
                .itemDueDate(dueDate)     // 3일 전에 반납 예정
                .itemReturnDate(returnDate)  // 2일 전에 반납됨
                .itemReturnImage("http://example.com/return_photo.png")
                .club(club)
                .build();
        itemHistoryRepository.save(itemHistory);
        return itemHistory;
    }

    private ClubMember createClubMember(Club club, Member member, ClubMemberRole memberRole, LocalDate date) {
        ClubMember clubMember = ClubMember.builder()
                .clubMemberAssignedTerm(dateUtil.getAssignedTerm(date))
                .club(club)
                .member(member)
                .clubMemberRole(memberRole)
                .build();
        return clubMemberRepository.save(clubMember);
    }
}