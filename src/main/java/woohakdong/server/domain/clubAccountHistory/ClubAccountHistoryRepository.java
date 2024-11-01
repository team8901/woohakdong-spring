package woohakdong.server.domain.clubAccountHistory;

import java.util.List;
import woohakdong.server.domain.clubAccount.ClubAccount;

public interface ClubAccountHistoryRepository {
    ClubAccountHistory save(ClubAccountHistory clubAccountHistory);

    void saveAll(List<ClubAccountHistory> clubAccountHistories);

    List<ClubAccountHistory> findMonthlyTransactions(ClubAccount clubAccount, int year, int month);
}
