package woohakdong.server.domain.clubHistory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.school.School;

public interface ClubHistoryJpaRepository extends JpaRepository<ClubHistory, Long> {
    List<ClubHistory> findAllByClub(Club club);

    Long countByClubHistoryUsageDate(LocalDate clubHistoryUsageDate);

    @Query("SELECT DISTINCT ch.club FROM ClubHistory ch WHERE ch.clubHistoryUsageDate = :clubHistoryUsageDate")
    List<Club> findDistinctClubsByClubHistoryUsageDate(@Param("clubHistoryUsageDate") LocalDate clubHistoryUsageDate);

    @Query("SELECT DISTINCT ch.club FROM ClubHistory ch WHERE ch.clubHistoryUsageDate = :clubHistoryUsageDate AND ch.club.school = :school")
    List<Club> findDistinctClubsByClubHistoryUsageDateAndSchool(@Param("clubHistoryUsageDate") LocalDate clubHistoryUsageDate,
                                                                @Param("school") School school);

    Long countByClub_SchoolAndClubHistoryUsageDate(School school, LocalDate assignedTerm);

}
