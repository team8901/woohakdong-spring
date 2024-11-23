package woohakdong.server.domain.clubHistory;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import woohakdong.server.domain.club.Club;

@RequiredArgsConstructor
@Repository
public class ClubHistoryRepositoryImpl implements ClubHistoryRepository {

    private final ClubHistoryJpaRepository clubHistoryJpaRepository;

    @Override
    public ClubHistory save(ClubHistory clubHistory) {
        return clubHistoryJpaRepository.save(clubHistory);
    }

    @Override
    public List<ClubHistory> getAllByClub(Club club) {
        return clubHistoryJpaRepository.findAllByClub(club);
    }

    @Override
    public Long countByClubHistoryUsageDate(LocalDate clubHistoryUsageDate) {
        return clubHistoryJpaRepository.countByClubHistoryUsageDate(clubHistoryUsageDate);
    }

    @Override
    public List<Club> getDistinctClubByClubHistoryUsageDate(LocalDate clubHistoryUsageDate) {
        return clubHistoryJpaRepository.findDistinctClubsByClubHistoryUsageDate(clubHistoryUsageDate);
    }
}
