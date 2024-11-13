package woohakdong.server.domain.club;

import woohakdong.server.domain.school.School;

import java.util.List;

public interface ClubRepository {
    Club save(Club club);

    Club getById(Long clubId);

    Boolean existsByClubNameOrClubEnglishName(String clubName, String clubEnglishName);

    Club getByClubEnglishName(String clubEnglishName);

    void validateClubExists(Long clubId);

    List<Club> getAll();

    Long count();

    Long countBySchool(School school);

    List<Club> getBySchool(School school);
}
