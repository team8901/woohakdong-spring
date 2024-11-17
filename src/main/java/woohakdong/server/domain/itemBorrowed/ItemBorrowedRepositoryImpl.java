package woohakdong.server.domain.itemBorrowed;

import static woohakdong.server.common.exception.CustomErrorInfo.ITEM_BORROWED_NOT_FOUND;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import woohakdong.server.common.exception.CustomException;
import woohakdong.server.domain.clubmember.ClubMember;
import woohakdong.server.domain.item.Item;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class ItemBorrowedRepositoryImpl implements ItemBorrowedRepository {

    private final ItemBorrowedJpaRepository itemBorrowedJpaRepository;

    @Override
    public List<ItemBorrowed> getByClubMember(ClubMember clubMember) {
        return itemBorrowedJpaRepository.findByClubMember(clubMember);
    }

    public void save(ItemBorrowed itemBorrowed) {
        itemBorrowedJpaRepository.save(itemBorrowed);
    }

    public void delete(ItemBorrowed itemBorrowed) {
        itemBorrowedJpaRepository.delete(itemBorrowed);
    }

    public ItemBorrowed getByItem(Item item) {
        return itemBorrowedJpaRepository.findByItem(item)
                .orElseThrow(() -> new CustomException(ITEM_BORROWED_NOT_FOUND));
    }

    public List<ItemBorrowed> getByItemBorrowedReturnDateBefore(LocalDateTime currentDateTime) {
        return itemBorrowedJpaRepository.findByItemBorrowedReturnDateBefore(currentDateTime);
    }
}
