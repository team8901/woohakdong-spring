package woohakdong.server.domain.clubHistory;

import java.time.LocalDate;
import java.util.List;
import woohakdong.server.domain.club.Club;

public interface ClubHistoryRepository {
    ClubHistory save(ClubHistory clubHistory);

    List<ClubHistory> getAllByClub(Club club);

    Long countByClubHistoryUsageDate(LocalDate clubHistoryUsageDate);

    List<Club> getDistinctClubByClubHistoryUsageDate(LocalDate clubHistoryUsageDate);
}
