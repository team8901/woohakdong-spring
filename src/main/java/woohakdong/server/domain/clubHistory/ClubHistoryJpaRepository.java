package woohakdong.server.domain.clubHistory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import woohakdong.server.domain.club.Club;

public interface ClubHistoryJpaRepository extends JpaRepository<ClubHistory, Long> {
    List<ClubHistory> findAllByClub(Club club);
}
