package woohakdong.server.api.service.item;

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
import woohakdong.server.domain.item.Item;
import woohakdong.server.domain.item.ItemCategory;
import woohakdong.server.domain.item.ItemRepository;
import woohakdong.server.domain.member.Member;
import woohakdong.server.domain.member.MemberRepository;

import static woohakdong.server.common.exception.CustomErrorInfo.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        assertThat(itemRepository.findById(response.itemId())).isPresent();
    }

    @DisplayName("물품리스트를 확인할 수 있다.")
    @Test
    void getItemsByClubId() {
        // given
        Club club = Club.builder()
                .clubName("테스트동아리")
                .clubEnglishName("testClub")
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
        List<ItemListResponse> items = itemService.getItemsByClubId(club.getClubId());

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
        Item borrowedItem = itemRepository.findById(item.getItemId()).orElseThrow();
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
        Item returnedItem = itemRepository.findById(item.getItemId()).orElseThrow();
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

}