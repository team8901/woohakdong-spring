package woohakdong.server.domain.clubAccount;

import static woohakdong.server.common.exception.CustomErrorInfo.CLUB_ACCOUNT_NOT_FOUND;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import woohakdong.server.common.exception.CustomException;
import woohakdong.server.domain.club.Club;

@RequiredArgsConstructor
@Repository
public class ClubAccountRepositoryImpl implements ClubAccountRepository {
    private final ClubAccountJpaRepository clubAccountJpaRepository;

    @Override
    public Boolean existsByClubAccountBankNameAndClubAccountNumber(String clubAccountBankName,
                                                                   String clubAccountNumber) {
        return clubAccountJpaRepository.existsByClubAccountBankNameAndClubAccountNumber(clubAccountBankName,
                clubAccountNumber);
    }

    @Override
    public ClubAccount getByClub(Club club) {
        return clubAccountJpaRepository.findByClub(club)
                .orElseThrow(() -> new CustomException(CLUB_ACCOUNT_NOT_FOUND));
    }

    @Override
    public ClubAccount save(ClubAccount clubAccount) {
        return clubAccountJpaRepository.save(clubAccount);
    }

    @Override
    public void delete(ClubAccount clubAccount) {
        clubAccountJpaRepository.delete(clubAccount);
    }

}
