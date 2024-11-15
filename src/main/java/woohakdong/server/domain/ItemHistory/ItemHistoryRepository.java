package woohakdong.server.domain.ItemHistory;

import java.util.List;

import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.item.Item;
import woohakdong.server.domain.member.Member;

public interface ItemHistoryRepository {
    ItemHistory save(ItemHistory itemHistory);

    ItemHistory getById(Long itemHistoryId);

    ItemHistory getActiveBorrowingRecord(Item item, Member member);

    List<ItemHistory> getAllByItem(Item item);

    List<ItemHistory> getAllByMember(Member member);

    List<ItemHistory> getAllByClubAndMember(Club club, Member member);
}
