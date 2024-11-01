package woohakdong.server.domain.item;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import woohakdong.server.domain.club.Club;

public interface ItemJpaRepository extends JpaRepository<Item, Long> {
    List<Item> findByClub(Club club);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Item i WHERE i.itemId = :itemId")
    Optional<Item> findByIdForUpdate(@Param("itemId") Long itemId);

    List<Item> findAllByClubAndItemCategory(Club club, ItemCategory category);

    List<Item> findAllByClubAndItemNameContaining(Club club, String itemName);

    List<Item> findAllByClubAndItemNameAndItemCategoryContaining(Club club, String keyword, ItemCategory category);
}
