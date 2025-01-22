package woohakdong.server.domain.item;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.query.Param;
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

    List<Item> getItemsByFilters(Club club, String keyword, ItemCategory category, Boolean using, Boolean available);

    Long countByClubSchoolAndCreatedAtBefore(School school, LocalDateTime dateTime);

    Long countByClubAndCreatedAtBefore(Club club, LocalDateTime dateTime);

    Long countByClub(Club club);

    Slice<Item> getAllByClub(Club club, Pageable pageable);

    Slice<Item> getItemsByFilters(Club club, String keyword, ItemCategory category, Boolean using, Boolean available, Pageable pageable);
}
