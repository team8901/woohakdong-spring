package woohakdong.server.domain.clubHistory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClubHistoryRepository extends JpaRepository<ClubHistory, Long> {
    List<ClubHistory> findByClub_ClubId(Long clubId);
}
