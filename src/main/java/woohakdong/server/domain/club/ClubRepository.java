package woohakdong.server.domain.club;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClubRepository extends JpaRepository<Club, Long> {

    Boolean existsByClubNameOrClubEnglishName(String clubName, String clubEnglishName);

    Optional<Club> findByClubEnglishName(String clubEnglishName);
}
