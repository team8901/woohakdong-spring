package woohakdong.server.domain.itemBorrowed;

import woohakdong.server.domain.clubmember.ClubMember;
import woohakdong.server.domain.item.Item;

import java.util.List;

public interface ItemBorrowedRepository {
    List<ItemBorrowed> findByClubMember(ClubMember clubMember);
    void save(ItemBorrowed itemBorrowed);
    void delete(ItemBorrowed itemBorrowed);
    ItemBorrowed findByItem(Item item);
}
