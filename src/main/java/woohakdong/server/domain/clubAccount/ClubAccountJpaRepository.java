package woohakdong.server.domain.clubAccount;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import woohakdong.server.domain.club.Club;

public interface ClubAccountJpaRepository extends JpaRepository<ClubAccount, Long> {
    Boolean existsByClubAccountBankNameAndClubAccountNumber(String clubAccountBankName, String clubAccountNumber);

    Optional<ClubAccount> findByClub(Club club);
}
