package woohakdong.server.domain.clubAccountHistory;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.query.Param;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.clubAccount.ClubAccount;

public interface ClubAccountHistoryRepository {
    ClubAccountHistory save(ClubAccountHistory clubAccountHistory);

    void saveAll(List<ClubAccountHistory> clubAccountHistories);

    List<ClubAccountHistory> findMonthlyTransactions(ClubAccount clubAccount, int year, int month);

    List<ClubAccountHistory> findAllByClubAccountClub(Club club);

    void deleteAllByClubAccount(ClubAccount clubAccount);

    Slice<ClubAccountHistory> getTransactionsByFilters(ClubAccount clubAccount, Integer year,
                                                       Integer month, String keyword, Pageable pageable);
}
