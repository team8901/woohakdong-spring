package woohakdong.server.domain.itemBorrowed;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import woohakdong.server.domain.clubmember.ClubMember;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class ItemBorrowedRepositoryImpl implements ItemBorrowedRepository {

    private final ItemBorrowedJpaRepository itemBorrowedJpaRepository;

    @Override
    public List<ItemBorrowed> findByClubMember(ClubMember clubMember) {
        return itemBorrowedJpaRepository.findByClubMember(clubMember);
    }

    public void save(ItemBorrowed itemBorrowed) {
        itemBorrowedJpaRepository.save(itemBorrowed);
    }
}
