package woohakdong.server.domain.clubHistory;

import java.util.List;
import woohakdong.server.domain.club.Club;

public interface ClubHistoryRepository {
    ClubHistory save(ClubHistory clubHistory);

    List<ClubHistory> getAllByClub(Club club);
}
