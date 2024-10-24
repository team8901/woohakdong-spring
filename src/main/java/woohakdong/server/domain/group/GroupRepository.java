package woohakdong.server.domain.group;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import woohakdong.server.domain.club.Club;

public interface GroupRepository extends JpaRepository<Group, Long> {

    // JOIN에 해당하는 Gathering을 찾는 메소드
    Optional<Group> findByClubAndGroupType(Club club, GroupType groupType);

    // Event에 해당하는 Gathering을 찾는 메소드
    List<Group> findAllByClubAndGroupType(Club club, GroupType groupType);
}
