package woohakdong.server.domain.itemBorrowed;

import org.springframework.data.jpa.repository.JpaRepository;
import woohakdong.server.domain.clubmember.ClubMember;

import java.util.List;

public interface ItemBorrowedJpaRepository extends JpaRepository<ItemBorrowed, Long> {
    List<ItemBorrowed> findByClubMember(ClubMember clubMember);
}
