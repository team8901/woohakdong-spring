package woohakdong.server.domain.club;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import woohakdong.server.domain.school.School;

public interface ClubJpaRepository extends JpaRepository<Club, Long> {

    Boolean existsByClubNameOrClubEnglishName(String clubName, String clubEnglishName);

    Optional<Club> findByClubEnglishName(String clubEnglishName);

    Long countBySchool(School school);

    List<Club> getBySchool(School school);
}
