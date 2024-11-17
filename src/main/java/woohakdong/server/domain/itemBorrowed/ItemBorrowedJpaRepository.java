package woohakdong.server.domain.itemBorrowed;

import org.springframework.data.jpa.repository.JpaRepository;
import woohakdong.server.domain.clubmember.ClubMember;
import woohakdong.server.domain.item.Item;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ItemBorrowedJpaRepository extends JpaRepository<ItemBorrowed, Long> {
    List<ItemBorrowed> findByClubMember(ClubMember clubMember);
    Optional<ItemBorrowed> findByItem(Item item);
    List<ItemBorrowed> findByItemBorrowedReturnDateBefore(LocalDateTime currentDateTime);
}
