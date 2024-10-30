package woohakdong.server.domain.clubAccountHistory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import woohakdong.server.domain.clubAccount.ClubAccount;

import java.util.List;

public interface ClubAccountHistoryRepository extends JpaRepository<ClubAccountHistory, Long> {

    @Query("SELECT h FROM ClubAccountHistory h " +
            "WHERE h.clubAccount = :clubAccount " +
            "AND FUNCTION('YEAR', h.clubAccountHistoryTranDate) = :year " +
            "AND FUNCTION('MONTH', h.clubAccountHistoryTranDate) = :month " +
            "ORDER BY h.clubAccountHistoryTranDate DESC")
    List<ClubAccountHistory> findMonthlyTransactions(@Param("clubAccount") ClubAccount clubAccount,
                                                     @Param("year") int year,
                                                     @Param("month") int month);
}
