package woohakdong.server.domain.ItemHistory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import woohakdong.server.domain.item.Item;
import woohakdong.server.domain.member.Member;

import java.util.Optional;

public interface ItemHistoryRepository extends JpaRepository<ItemHistory, Long> {
    @Query("SELECT ih FROM ItemHistory ih WHERE ih.item = :item AND ih.member = :member AND ih.itemReturnDate IS NULL")
    Optional<ItemHistory> findActiveBorrowingRecord(@Param("item") Item item, @Param("member") Member member);
}
