package woohakdong.server.domain.ItemHistory;

import java.time.LocalDateTime;
import java.util.List;

import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.clubmember.ClubMember;
import woohakdong.server.domain.item.Item;
import woohakdong.server.domain.member.Member;

public interface ItemHistoryRepository {
    ItemHistory save(ItemHistory itemHistory);

    ItemHistory getById(Long itemHistoryId);

    ItemHistory getActiveBorrowingRecord(Item item, ClubMember clubMember);

    List<ItemHistory> getAllByItem(Item item);

    List<ItemHistory> getAllByMember(ClubMember clubMember);

    List<ItemHistory> getAllByClubAndMember(Club club, ClubMember clubMember);

    ItemHistory getByItemAndItemReturnDateIsNull(Item item);

    List<ItemHistory> getByItemClub(Club club);

    List<ItemHistory> getByItemClubAndItemRentalDateBetween(Club club, LocalDateTime startDate, LocalDateTime endDate);

    List<ItemHistory> getByClub(Club club);
}
