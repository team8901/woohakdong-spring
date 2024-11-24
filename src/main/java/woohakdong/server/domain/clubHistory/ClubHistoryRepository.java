package woohakdong.server.domain.clubHistory;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.repository.query.Param;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.school.School;

public interface ClubHistoryRepository {
    ClubHistory save(ClubHistory clubHistory);

    List<ClubHistory> getAllByClub(Club club);

    Long countByClubHistoryUsageDate(LocalDate clubHistoryUsageDate);

    List<Club> getDistinctClubByClubHistoryUsageDate(LocalDate clubHistoryUsageDate);

    Long countByClub_SchoolAndClubHistoryUsageDate(School school, LocalDate assignedTerm);

    List<Club> getDistinctClubsByClubHistoryUsageDateAndSchool(LocalDate clubHistoryUsageDate, School school);
}
