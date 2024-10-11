package woohakdong.server.domain.clubAccount;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ClubAccountRepository extends JpaRepository<ClubAccount, Long> {
    Boolean existsByClubAccountBankNameAndClubAccountNumber(String clubAccountBankName, String clubAccountNumber);
}
