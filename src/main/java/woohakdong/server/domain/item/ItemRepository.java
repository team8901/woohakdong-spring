package woohakdong.server.domain.item;

import java.util.List;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.school.School;

public interface ItemRepository {
    Item save(Item item);

    Item getById(Long itemId);

    void delete(Item item);

    List<Item> getAllByClub(Club club);

    Item getByIdForUpdate(Long itemId);

    List<Item> getAllByClubAndItemCategory(Club club, ItemCategory category);

    List<Item> getAllByClubAndNameContaining(Club club, String itemName);

    List<Item> getAllByClubAndItemNameAndItemCategoryContaining(Club club, ItemCategory category, String keyword);

    Long countByClubSchool(School school);
}
