package woohakdong.server.domain.gathering;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import woohakdong.server.domain.club.Club;

public interface GatheringRepository extends JpaRepository<Gathering, Long> {

    // JOIN에 해당하는 Gathering을 찾는 메소드
    Optional<Gathering> findByClubAndGatheringType(Club club, GatheringType gatheringType);

    // Event에 해당하는 Gathering을 찾는 메소드
    List<Gathering>findAllByClubAndGatheringType(Club club, GatheringType gatheringType);
}
