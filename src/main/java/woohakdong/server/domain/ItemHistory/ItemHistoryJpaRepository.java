package woohakdong.server.domain.ItemHistory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import woohakdong.server.domain.item.Item;
import woohakdong.server.domain.member.Member;

public interface ItemHistoryJpaRepository extends JpaRepository<ItemHistory, Long> {
    Optional<ItemHistory> findByItemAndMemberAndItemReturnDateIsNull(Item item, Member member);

    List<ItemHistory> findByItem(Item item);

    List<ItemHistory> findByMember(Member member);
}