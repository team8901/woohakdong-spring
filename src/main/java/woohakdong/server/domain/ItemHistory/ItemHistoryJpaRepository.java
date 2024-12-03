package woohakdong.server.domain.ItemHistory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.clubmember.ClubMember;
import woohakdong.server.domain.item.Item;
import woohakdong.server.domain.member.Member;

public interface ItemHistoryJpaRepository extends JpaRepository<ItemHistory, Long> {
    Optional<ItemHistory> findByItemAndClubMemberAndItemReturnDateIsNull(Item item, ClubMember clubMember);

    List<ItemHistory> findByItemOrderByItemRentalDateDesc(Item item);

    List<ItemHistory> findByClubMemberOrderByItemRentalDateDesc(ClubMember clubMember);

    List<ItemHistory> findByItemClubAndClubMemberOrderByItemRentalDateDesc(Club club, ClubMember clubMember);

    Optional<ItemHistory> findByItemAndItemReturnDateIsNull(Item item);

    List<ItemHistory> findByItemClub(Club club);

    List<ItemHistory> findByItemClubAndItemRentalDateBetween(Club club, LocalDateTime startDate, LocalDateTime endDate);

    List<ItemHistory> findByClubOrderByItemRentalDateDesc(Club club);
}
