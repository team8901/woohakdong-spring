package woohakdong.server.domain.ItemHistory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import woohakdong.server.domain.item.Item;
import woohakdong.server.domain.member.Member;

import java.util.List;
import java.util.Optional;

public interface ItemHistoryRepository extends JpaRepository<ItemHistory, Long> {
    @Query("SELECT ih FROM ItemHistory ih WHERE ih.item.itemId = :itemId AND ih.member.memberId = :memberId AND ih.itemReturnDate IS NULL")
    Optional<ItemHistory> findActiveBorrowingRecord(@Param("itemId") Long itemId, @Param("memberId") Long memberId);

    @Query("SELECT ih FROM ItemHistory ih WHERE ih.item.itemId = :itemId AND ih.item.club.clubId = :clubId")
    List<ItemHistory> findByItemAndClub(@Param("itemId") Long itemId, @Param("clubId") Long clubId);
}
