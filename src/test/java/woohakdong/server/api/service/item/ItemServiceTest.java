package woohakdong.server.api.service.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import woohakdong.server.api.controller.club.dto.ClubCreateRequest;
import woohakdong.server.api.controller.club.dto.ClubCreateResponse;
import woohakdong.server.api.controller.item.dto.ItemListResponse;
import woohakdong.server.api.controller.item.dto.ItemRegisterRequest;
import woohakdong.server.api.controller.item.dto.ItemRegisterResponse;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.club.ClubRepository;
import woohakdong.server.domain.item.Item;
import woohakdong.server.domain.item.ItemCategory;
import woohakdong.server.domain.item.ItemRepository;
import woohakdong.server.domain.school.School;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static woohakdong.server.domain.group.GroupType.JOIN;

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
                "Club Storage Room", ItemCategory.SPORTS, 7
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
                "Club Storage Room", ItemCategory.SPORTS, 7
        ));
        itemService.registerItem(club.getClubId(), new ItemRegisterRequest(
                "농구공", "http://example.com/basketball.png", "A standard basketball",
                "Gym", ItemCategory.SPORTS, 5
        ));

        // When: 클럽 ID로 물품 리스트 조회
        List<ItemListResponse> items = itemService.getItemsByClubId(club.getClubId());

        // Then: 물품 리스트가 제대로 조회되었는지 확인
        assertThat(items).hasSize(2);
        assertThat(items.get(0).itemName()).isEqualTo("축구공");
        assertThat(items.get(1).itemName()).isEqualTo("농구공");
    }
}