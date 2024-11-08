package woohakdong.server.domain.itemBorrowed;

import woohakdong.server.domain.clubmember.ClubMember;

import java.util.List;

public interface ItemBorrowedRepository {
    List<ItemBorrowed> findByClubMember(ClubMember clubMember);
    void save(ItemBorrowed itemBorrowed);
}
