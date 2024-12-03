package woohakdong.server.domain.clubAccountHistory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.clubAccount.ClubAccount;

import java.util.List;

public interface ClubAccountHistoryJpaRepository extends JpaRepository<ClubAccountHistory, Long> {

    @Query("SELECT h FROM ClubAccountHistory h " +
            "WHERE h.clubAccount = :clubAccount " +
            "AND FUNCTION('YEAR', h.clubAccountHistoryTranDate) = :year " +
            "AND FUNCTION('MONTH', h.clubAccountHistoryTranDate) = :month " +
            "ORDER BY h.clubAccountHistoryTranDate DESC")
    List<ClubAccountHistory> findMonthlyTransactions(@Param("clubAccount") ClubAccount clubAccount,
                                                     @Param("year") int year,
                                                     @Param("month") int month);

    @Query("SELECT cah FROM ClubAccountHistory cah " +
            "WHERE cah.clubAccount = :clubAccount " +
            "AND (:year IS NULL OR YEAR(cah.clubAccountHistoryTranDate) = :year) " +
            "AND (:month IS NULL OR MONTH(cah.clubAccountHistoryTranDate) = :month) " +
            "AND (:keyword IS NULL OR STR(cah.clubAccountHistoryTranAmount) LIKE %:keyword%)" +
            "ORDER BY cah.clubAccountHistoryTranDate DESC")
    List<ClubAccountHistory> findTransactionsByFilters(@Param("clubAccount") ClubAccount clubAccount,
                                                       @Param("year") Integer year,
                                                       @Param("month") Integer month,
                                                       @Param("keyword") String keyword);

    List<ClubAccountHistory> findAllByClubAccountClubOrderByClubAccountHistoryTranDateDesc(Club club);

    void deleteAllByClubAccount(ClubAccount clubAccount);
}
