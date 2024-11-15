package woohakdong.server.domain.clubAccountHistory;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.clubAccount.ClubAccount;

@RequiredArgsConstructor
@Repository
public class ClubAccountHistoryRepositoryImpl implements ClubAccountHistoryRepository{

    private final ClubAccountHistoryJpaRepository clubAccountHistoryJpaRepository;

    @Override
    public ClubAccountHistory save(ClubAccountHistory clubAccountHistory) {
        return clubAccountHistoryJpaRepository.save(clubAccountHistory);
    }

    @Override
    public void saveAll(List<ClubAccountHistory> clubAccountHistories) {
        clubAccountHistoryJpaRepository.saveAll(clubAccountHistories);
    }


    @Override
    public List<ClubAccountHistory> findMonthlyTransactions(ClubAccount clubAccount, int year, int month) {
        return clubAccountHistoryJpaRepository.findMonthlyTransactions(clubAccount, year, month);
    }

    @Override
    public List<ClubAccountHistory> findAllByClubAccountClub(Club club) {
        return clubAccountHistoryJpaRepository.findAllByClubAccountClubOrderByClubAccountHistoryTranDateDesc(club);
    }
}
