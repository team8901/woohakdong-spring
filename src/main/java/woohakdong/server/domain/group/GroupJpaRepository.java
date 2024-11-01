package woohakdong.server.domain.group;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import woohakdong.server.domain.club.Club;

public interface GroupJpaRepository extends JpaRepository<Group, Long> {

    // JOIN에 해당하는 Gathering을 찾는 메소드
    Optional<Group> findByClubAndGroupTypeAndGroupIsAvailable(Club club, GroupType groupType, Boolean groupIsAvailable);

    // Event에 해당하는 Gathering을 찾는 메소드
    List<Group> findAllByClubAndGroupType(Club club, GroupType groupType);
}
