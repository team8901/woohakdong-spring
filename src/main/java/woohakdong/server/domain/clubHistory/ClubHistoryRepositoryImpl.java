package woohakdong.server.domain.clubHistory;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.school.School;

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

    @Override
    public Long countByClub_SchoolAndClubHistoryUsageDate(School school, LocalDate assignedTerm) {
        return clubHistoryJpaRepository.countByClub_SchoolAndClubHistoryUsageDate(school, assignedTerm);
    }

    @Override
    public List<Club> getDistinctClubsByClubHistoryUsageDateAndSchool(LocalDate clubHistoryUsageDate, School school) {
        return clubHistoryJpaRepository.findDistinctClubsByClubHistoryUsageDateAndSchool(clubHistoryUsageDate, school);
    }
}
