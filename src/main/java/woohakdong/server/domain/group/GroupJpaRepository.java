package woohakdong.server.domain.group;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import woohakdong.server.domain.club.Club;

public interface GroupJpaRepository extends JpaRepository<Group, Long> {

    Optional<Group> findByClubAndGroupType(Club club, GroupType groupType);

    List<Group> findAllByClubAndGroupType(Club club, GroupType groupType);

    boolean existsByClubAndGroupType(Club club, GroupType groupType);
}
