package woohakdong.server.domain.item;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.school.School;

public interface ItemJpaRepository extends JpaRepository<Item, Long> {
    List<Item> findByClub(Club club);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Item i WHERE i.itemId = :itemId")
    Optional<Item> findByIdForUpdate(@Param("itemId") Long itemId);

    List<Item> findAllByClubAndItemCategory(Club club, ItemCategory category);

    List<Item> findAllByClubAndItemNameContaining(Club club, String itemName);

    List<Item> findAllByClubAndItemNameAndItemCategoryContaining(Club club, String keyword, ItemCategory category);

    Long countByClubSchool(School school);

    @Query("SELECT i FROM Item i " +
            "WHERE i.club = :club " +
            "AND (:keyword IS NULL OR i.itemName LIKE %:keyword%) " +
            "AND (:category IS NULL OR i.itemCategory = :category) " +
            "AND (:using IS NULL OR i.itemUsing = :using) " +
            "AND (:available IS NULL OR i.itemAvailable = :available)")
    List<Item> findItemsByFilters(@Param("club") Club club,
                                  @Param("keyword") String keyword,
                                  @Param("category") ItemCategory category,
                                  @Param("using") Boolean using,
                                  @Param("available") Boolean available);

    Long countByClubSchoolAndCreatedAtBefore(School school, LocalDateTime dateTime);

    Long countByClubAndCreatedAtBefore(Club club, LocalDateTime dateTime);

    Long countByClub(Club club);

    Slice<Item> findByClub(Club club, Pageable pageable);

    @Query("SELECT i FROM Item i " +
            "WHERE i.club = :club " +
            "AND (:keyword IS NULL OR i.itemName LIKE %:keyword%) " +
            "AND (:category IS NULL OR i.itemCategory = :category) " +
            "AND (:using IS NULL OR i.itemUsing = :using) " +
            "AND (:available IS NULL OR i.itemAvailable = :available)")
    Slice<Item> findItemsByFilters(@Param("club") Club club,
                                  @Param("keyword") String keyword,
                                  @Param("category") ItemCategory category,
                                  @Param("using") Boolean using,
                                  @Param("available") Boolean available,
                                   Pageable pageable);
}
