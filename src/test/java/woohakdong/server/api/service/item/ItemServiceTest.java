package woohakdong.server.api.service.item;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static woohakdong.server.common.exception.CustomErrorInfo.ITEM_USING;

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
import woohakdong.server.api.controller.item.dto.ItemAvailableUpdateRequest;
import woohakdong.server.api.controller.item.dto.ItemHistoryResponse;
import woohakdong.server.api.controller.item.dto.ItemListResponse;
import woohakdong.server.api.controller.item.dto.ItemRegisterRequest;
import woohakdong.server.api.controller.item.dto.ItemRegisterResponse;
import woohakdong.server.api.controller.item.dto.ItemReturnRequest;
import woohakdong.server.api.controller.item.dto.ItemReturnResponse;
import woohakdong.server.api.controller.item.dto.ItemUpdateRequest;
import woohakdong.server.common.exception.CustomException;
import woohakdong.server.common.security.jwt.CustomUserDetails;
import woohakdong.server.domain.ItemHistory.ItemHistory;
import woohakdong.server.domain.ItemHistory.ItemHistoryRepository;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.club.ClubRepository;
import woohakdong.server.domain.item.Item;
import woohakdong.server.domain.item.ItemCategory;
import woohakdong.server.domain.item.ItemRepository;
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

    @DisplayName("물품을 등록하면 물품을 확인할 수 있다.")
    @Test
    void registerItem() {
        // given
        Club club = Club.builder()
                .clubName("테스트동아리")
                .clubEnglishName("testClub")
                .clubGroupChatLink("https://club-group-chat-link.com")
                .build();
        clubRepository.save(club);

        ItemRegisterRequest request = new ItemRegisterRequest(
                "축구공", "http://example.com/soccer_ball.png", "A standard soccer ball",
                "Club Storage Room", ItemCategory.SPORT, 7
        );

        // When
        ItemRegisterResponse response = itemService.registerItem(club.getClubId(), request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.itemId()).isNotNull();
        assertThat(response.itemName()).isEqualTo("축구공");

        // 그리고 DB에 물품이 실제로 저장되었는지 확인
        assertThat(itemRepository.getById(response.itemId())).isNotNull();
    }

    @DisplayName("물품리스트를 확인할 수 있다.")
    @Test
    void getItemsByClubId() {
        // given
        Club club = Club.builder()
                .clubName("테스트동아리")
                .clubEnglishName("testClub")
                .clubGroupChatLink("https://club-group-chat-link.com")
                .build();
        clubRepository.save(club);

        itemService.registerItem(club.getClubId(), new ItemRegisterRequest(
                "축구공", "http://example.com/soccer_ball.png", "A standard soccer ball",
                "Club Storage Room", ItemCategory.SPORT, 7
        ));
        itemService.registerItem(club.getClubId(), new ItemRegisterRequest(
                "농구공", "http://example.com/basketball.png", "A standard basketball",
                "Gym", ItemCategory.SPORT, 5
        ));

        // When: 클럽 ID로 물품 리스트 조회
        List<ItemListResponse> items = itemService.getItemsByFilters(club.getClubId(), null, null);

        // Then: 물품 리스트가 제대로 조회되었는지 확인
        assertThat(items).hasSize(2);
        assertThat(items.get(0).itemName()).isEqualTo("축구공");
        assertThat(items.get(1).itemName()).isEqualTo("농구공");
    }

    @DisplayName("물품 대여를 성공적으로 할 수 있다.")
    @Test
    void borrowItemSuccess() {
        // given
        Club club = Club.builder()
                .clubName("테스트동아리")
                .clubEnglishName("testClub")
                .clubGroupChatLink("https://club-group-chat-link.com")
                .build();
        clubRepository.save(club);

        String provideId = "testProvideId";
        Member member = Member.builder()
                .memberProvideId(provideId)
                .memberName("John Doe")
                .memberEmail("john.doe@example.com")
                .build();
        memberRepository.save(member);

        Item item = itemRepository.save(Item.builder()
                .club(club)
                .itemName("축구공")
                .itemPhoto("http://example.com/soccer_ball.png")
                .itemDescription("A standard size 5 soccer ball")
                .itemLocation("Club Storage Room")
                .itemCategory(ItemCategory.SPORT)
                .itemRentalMaxDay(7)
                .itemAvailable(true)
                .itemUsing(false)
                .itemRentalTime(0)
                .build());

        String role = "USER_ROLE";
        CustomUserDetails customUserDetails = new CustomUserDetails(provideId, role);
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // when
        itemService.borrowItem(club.getClubId(), item.getItemId());

        //then
        Item borrowedItem = itemRepository.getById(item.getItemId());
        assertThat(borrowedItem.getItemUsing()).isTrue();
        assertThat(borrowedItem.getItemAvailable()).isTrue(); // 여전히 대여 가능 상태 (단, 현재 사용 중)

        // 대여 기록이 잘 생성되었는지 확인 (대여 시간 확인)
        assertThat(borrowedItem.getItemRentalDate()).isBefore(LocalDateTime.now());
    }

    @DisplayName("이미 대여된 물품은 대여를 할 수 없다.")
    @Test
    void borrowItemAlreadyInUseFailure() {
        // given
        Club club = Club.builder()
                .clubName("테스트동아리")
                .clubEnglishName("testClub")
                .clubGroupChatLink("https://club-group-chat-link.com")
                .build();
        clubRepository.save(club);

        String provideId = "testProvideId";
        Member member = Member.builder()
                .memberProvideId(provideId)
                .memberName("John Doe")
                .memberEmail("john.doe@example.com")
                .build();
        memberRepository.save(member);

        Item item = itemRepository.save(Item.builder()
                .club(club)
                .itemName("축구공")
                .itemPhoto("http://example.com/soccer_ball.png")
                .itemDescription("A standard size 5 soccer ball")
                .itemLocation("Club Storage Room")
                .itemCategory(ItemCategory.SPORT)
                .itemRentalMaxDay(7)
                .itemAvailable(true)
                .itemUsing(true)
                .itemRentalTime(0)
                .build());

        String role = "USER_ROLE";
        CustomUserDetails customUserDetails = new CustomUserDetails(provideId, role);
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // when & then
        assertThatThrownBy(() -> itemService.borrowItem(club.getClubId(), item.getItemId()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ITEM_USING.getMessage());
    }

    @DisplayName("물품을 성공적으로 반납할 수 있다.")
    @Test
    void returnItemSuccess() {
        // given
        Club club = Club.builder()
                .clubName("테스트동아리")
                .clubEnglishName("testClub")
                .clubGroupChatLink("https://club-group-chat-link.com")
                .build();
        clubRepository.save(club);

        String provideId = "testProvideId";
        Member member = Member.builder()
                .memberProvideId(provideId)
                .memberName("John Doe")
                .memberEmail("john.doe@example.com")
                .build();
        memberRepository.save(member);

        Item item = itemRepository.save(Item.builder()
                .club(club)
                .itemName("축구공")
                .itemPhoto("http://example.com/soccer_ball.png")
                .itemDescription("A standard size 5 soccer ball")
                .itemLocation("Club Storage Room")
                .itemCategory(ItemCategory.SPORT)
                .itemRentalMaxDay(7)
                .itemAvailable(true)
                .itemUsing(false)
                .itemRentalTime(0)
                .build());

        String role = "USER_ROLE";
        CustomUserDetails customUserDetails = new CustomUserDetails(provideId, role);
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        ItemReturnRequest request = new ItemReturnRequest("http://example.com/return_photo.png");

        itemService.borrowItem(club.getClubId(), item.getItemId());

        // when
        ItemReturnResponse itemReturnResponse = itemService.returnItem(club.getClubId(), item.getItemId(), request);

        // then
        Item returnedItem = itemRepository.getById(item.getItemId());
        assertThat(returnedItem.getItemUsing()).isFalse();
        assertThat(returnedItem.getItemAvailable()).isTrue();

        // 반납 기록이 잘 저장되었는지 확인
        ItemHistory history = itemHistoryRepository.findById(itemReturnResponse.itemHistoryId()).orElseThrow();
        assertThat(history.getItemReturnDate()).isBefore(LocalDateTime.now());
    }

    @DisplayName("물품대여 기록을 조회할 수 있다.")
    @Test
    void getItemHistorySuccess() {
        // given
        Club club = Club.builder()
                .clubName("테스트동아리")
                .clubEnglishName("testClub")
                .clubGroupChatLink("https://club-group-chat-link.com")
                .build();
        clubRepository.save(club);

        String provideId = "testProvideId";
        Member member = Member.builder()
                .memberProvideId(provideId)
                .memberName("John Doe")
                .memberEmail("john.doe@example.com")
                .build();
        memberRepository.save(member);

        Item item = itemRepository.save(Item.builder()
                .club(club)
                .itemName("축구공")
                .itemPhoto("http://example.com/soccer_ball.png")
                .itemDescription("A standard size 5 soccer ball")
                .itemLocation("Club Storage Room")
                .itemCategory(ItemCategory.SPORT)
                .itemRentalMaxDay(7)
                .itemAvailable(true)
                .itemUsing(false)
                .itemRentalTime(0)
                .build());

        // 대여 기록 생성
        itemHistoryRepository.save(ItemHistory.builder()
                .item(item)
                .member(member)
                .itemRentalDate(LocalDateTime.now().minusDays(10)) // 10일 전 대여
                .itemDueDate(LocalDateTime.now().minusDays(3))     // 3일 전에 반납 예정
                .itemReturnDate(LocalDateTime.now().minusDays(2))  // 2일 전에 반납됨
                .itemReturnImage("http://example.com/return_photo.png")
                .build());

        itemHistoryRepository.save(ItemHistory.builder()
                .item(item)
                .member(member)
                .itemRentalDate(LocalDateTime.now())
                .itemDueDate(LocalDateTime.now().plusDays(7))
                .build());

        // when
        ListWrapperResponse<ItemHistoryResponse> response = itemService.getItemHistory(club.getClubId(), item.getItemId());

        // then
        assertThat(response).isNotNull();
        assertThat(response.result()).hasSize(2);

        // 첫 번째 기록 (반납된 기록)
        ItemHistoryResponse returnedHistory = response.result().get(0);
        assertThat(returnedHistory.itemHistoryId()).isNotNull();
        assertThat(returnedHistory.memberName()).isEqualTo("John Doe");
        assertThat(returnedHistory.itemRentalDate()).isBefore(LocalDateTime.now());
        assertThat(returnedHistory.itemDueDate()).isBefore(LocalDateTime.now());
        assertThat(returnedHistory.itemReturnDate()).isNotNull();  // 반납된 기록
        assertThat(returnedHistory.itemReturnImage()).isEqualTo("http://example.com/return_photo.png");

        // 두 번째 기록 (대여 중인 기록)
        ItemHistoryResponse borrowingHistory = response.result().get(1);
        assertThat(borrowingHistory.itemHistoryId()).isNotNull();
        assertThat(borrowingHistory.memberName()).isEqualTo("John Doe");
        assertThat(borrowingHistory.itemRentalDate()).isBefore(LocalDateTime.now());
        assertThat(borrowingHistory.itemDueDate()).isAfter(LocalDateTime.now());
        assertThat(borrowingHistory.itemReturnDate()).isNull();  // 아직 반납되지 않음
    }

    @DisplayName("물품이름으로 검색할 수 있다.")
    @Test
    void searchItemsByName_success() {
        // given
        Club club = Club.builder()
                .clubName("테스트동아리")
                .clubEnglishName("testClub")
                .clubGroupChatLink("https://club-group-chat-link.com")
                .build();
        clubRepository.save(club);

        String provideId = "testProvideId";
        Member member = Member.builder()
                .memberProvideId(provideId)
                .memberName("John Doe")
                .memberEmail("john.doe@example.com")
                .build();
        memberRepository.save(member);

        Item item = itemRepository.save(Item.builder()
                .club(club)
                .itemName("축구공")
                .itemPhoto("http://example.com/soccer_ball.png")
                .itemDescription("A standard size 5 soccer ball")
                .itemLocation("Club Storage Room")
                .itemCategory(ItemCategory.SPORT)
                .itemRentalMaxDay(7)
                .itemAvailable(true)
                .itemUsing(false)
                .itemRentalTime(0)
                .build());

        itemRepository.save(Item.builder()
                .club(club)
                .itemName("농구공")
                .itemPhoto("http://example.com/basketball.png")
                .itemDescription("A standard size basketball")
                .itemLocation("Storage Room B")
                .itemCategory(ItemCategory.SPORT)
                .itemAvailable(true)
                .itemUsing(false)
                .itemRentalMaxDay(7)
                .build());

        // when
        List<ItemListResponse> items = itemService.getItemsByFilters(club.getClubId(), "공", "");

        // then
        assertThat(items).hasSize(2);
        assertThat(items).extracting("itemName").containsExactlyInAnyOrder("축구공", "농구공");
    }

    @DisplayName("물품 이용 가능을 수정할 수 있다.")
    @Test
    void updateItemAvailability_success() {
        // given
        Club club = Club.builder()
                .clubName("테스트동아리")
                .clubEnglishName("testClub")
                .clubGroupChatLink("https://club-group-chat-link.com")
                .build();
        clubRepository.save(club);

        String provideId = "testProvideId";
        Member member = Member.builder()
                .memberProvideId(provideId)
                .memberName("John Doe")
                .memberEmail("john.doe@example.com")
                .build();
        memberRepository.save(member);

        Item item = itemRepository.save(Item.builder()
                .club(club)
                .itemName("축구공")
                .itemPhoto("http://example.com/soccer_ball.png")
                .itemDescription("A standard size 5 soccer ball")
                .itemLocation("Club Storage Room")
                .itemCategory(ItemCategory.SPORT)
                .itemRentalMaxDay(7)
                .itemAvailable(true)
                .itemUsing(false)
                .itemRentalTime(0)
                .build());


        // when
        itemService.updateItemAvailability(club.getClubId(), item.getItemId(), new ItemAvailableUpdateRequest(false));

        // Then
        Item updatedItem = itemRepository.getById(item.getItemId());
        assertThat(updatedItem.getItemAvailable()).isFalse();
    }

    @Test
    void updateItem_success() {
        // given
        Club club = Club.builder()
                .clubName("테스트동아리")
                .clubEnglishName("testClub")
                .clubGroupChatLink("https://club-group-chat-link.com")
                .build();
        clubRepository.save(club);

        String provideId = "testProvideId";
        Member member = Member.builder()
                .memberProvideId(provideId)
                .memberName("John Doe")
                .memberEmail("john.doe@example.com")
                .build();
        memberRepository.save(member);

        Item item = itemRepository.save(Item.builder()
                .club(club)
                .itemName("축구공")
                .itemPhoto("http://example.com/soccer_ball.png")
                .itemDescription("A standard size 5 soccer ball")
                .itemLocation("Club Storage Room")
                .itemCategory(ItemCategory.SPORT)
                .itemRentalMaxDay(7)
                .itemAvailable(true)
                .itemUsing(false)
                .itemRentalTime(0)
                .build());

        ItemUpdateRequest updateRequest = new ItemUpdateRequest(
                "새로운 축구공",
                "http://example.com/new_soccer_ball.png",
                "Updated soccer ball description",
                "Updated Location",
                ItemCategory.SPORT,
                9
        );

        // When
        itemService.updateItem(club.getClubId(), item.getItemId(), updateRequest);

        // Then
        Item updatedItem = itemRepository.getById(item.getItemId());
        assertThat(updatedItem.getItemName()).isEqualTo("새로운 축구공");
        assertThat(updatedItem.getItemPhoto()).isEqualTo("http://example.com/new_soccer_ball.png");
        assertThat(updatedItem.getItemDescription()).isEqualTo("Updated soccer ball description");
        assertThat(updatedItem.getItemLocation()).isEqualTo("Updated Location");
        assertThat(updatedItem.getItemRentalMaxDay()).isEqualTo(9);
    }

}