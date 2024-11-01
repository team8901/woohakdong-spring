package woohakdong.server.domain.ItemHistory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.item.Item;
import woohakdong.server.domain.member.Member;

public interface ItemJpaHistoryRepository extends JpaRepository<ItemHistory, Long> {
    Optional<ItemHistory> findByItemAndMemberAndItemReturnDateIsNull(Item item, Member member);

    List<ItemHistory> findByItem(Item item);
}
