package woohakdong.server.domain.item;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByClubClubId(Long clubId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Item i WHERE i.itemId = :itemId")
    Optional<Item> findByIdForUpdate(@Param("itemId") Long itemId);

    @Query("SELECT i FROM Item i WHERE i.club.clubId = :clubId AND i.itemName LIKE %:itemName%")
    List<Item> findItemsByClubIdAndNameContaining(@Param("clubId") Long clubId, @Param("itemName") String itemName);

    @Query("SELECT i FROM Item i WHERE i.club.clubId = :clubId AND i.itemName LIKE %:keyword% AND i.itemCategory = :category")
    List<Item> findByClubIdAndItemNameContainingAndItemCategory(@Param("clubId") Long clubId, @Param("keyword") String keyword, @Param("category") ItemCategory category);

}
