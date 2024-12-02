package woohakdong.server.domain.item;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import woohakdong.server.common.exception.CustomErrorInfo;
import woohakdong.server.common.exception.CustomException;
import woohakdong.server.RepositoryTestSetup;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.school.School;

class ItemRepositoryTest extends RepositoryTestSetup {

    @Autowired
    private ItemRepository itemRepository;

    @DisplayName("물품을 저장할 수 있다.")
    @Test
    void save() {
        // Given
        School school = createSchool();
        Club club = createClub(school);
        Item item = createItem(club, "테스트 물품", ItemCategory.BOOK);

        // When
        Item savedItem = itemRepository.save(item);

        // Then
        assertThat(savedItem)
                .extracting("itemName", "itemCategory")
                .containsExactly("테스트 물품", ItemCategory.BOOK);
    }

    @DisplayName("물품을 삭제할 수 있다")
    @Test
    void delete() {
        // Given
        School school = createSchool();
        Club club = createClub(school);
        Item item = createItem(club, "테스트 물품", ItemCategory.BOOK);
        Item savedItem = itemRepository.save(item);

        // When
        itemRepository.delete(savedItem);

        // Then
        assertThatThrownBy(() -> itemRepository.getById(savedItem.getItemId()))
                .isInstanceOf(CustomException.class)
                .hasMessage(CustomErrorInfo.ITEM_NOT_FOUND.getMessage());
    }

    private static Item createItem(Club club, String itemName, ItemCategory itemCategory) {
        return Item.builder()
                .itemName(itemName)
                .itemPhoto("https://item-image.com")
                .itemAvailable(true)
                .itemUsing(false)
                .itemCategory(itemCategory)
                .itemRentalMaxDay(7)
                .club(club)
                .build();
    }

    private static Club createClub(School school) {
        return Club.builder()
                .clubName("테스트 동아리")
                .clubEnglishName("testclub")
                .clubGroupChatLink("https://group-chat.com")
                .school(school)
                .build();
    }

    private static School createSchool() {
        return School.builder()
                .schoolName("테스트 학교")
                .schoolDomain("test.ac.kr")
                .build();
    }

}