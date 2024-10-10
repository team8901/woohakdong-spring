package woohakdong.server.domain.club;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ClubRepository extends JpaRepository<Club, Long> {

    Boolean existsByClubNameOrClubEnglishName(String clubName, String clubEnglishName);
}
