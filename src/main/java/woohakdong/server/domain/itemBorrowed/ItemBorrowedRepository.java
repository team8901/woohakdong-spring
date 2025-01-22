package woohakdong.server.domain.itemBorrowed;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import woohakdong.server.api.controller.SliceResponse;
import woohakdong.server.domain.clubmember.ClubMember;
import woohakdong.server.domain.item.Item;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemBorrowedRepository {
    List<ItemBorrowed> getByClubMember(ClubMember clubMember);

    void save(ItemBorrowed itemBorrowed);

    void delete(ItemBorrowed itemBorrowed);

    ItemBorrowed getByItem(Item item);

    List<ItemBorrowed> getByItemBorrowedReturnDateBefore(LocalDateTime currentDateTime);

    Slice<ItemBorrowed> getByClubMember(ClubMember clubMember, Pageable pageable);
}
