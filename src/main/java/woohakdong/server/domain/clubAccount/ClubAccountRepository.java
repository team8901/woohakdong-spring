package woohakdong.server.domain.clubAccount;

import woohakdong.server.domain.club.Club;

public interface ClubAccountRepository {
    Boolean existsByClubAccountBankNameAndClubAccountNumber(String clubAccountBankName, String clubAccountNumber);

    ClubAccount getByClub(Club club);

    ClubAccount save(ClubAccount clubAccount);

    void delete(ClubAccount clubAccount);
}
