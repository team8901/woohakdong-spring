package woohakdong.server.domain.club;

import java.util.List;

public interface ClubRepository {
    Club save(Club club);

    Club getById(Long clubId);

    Boolean existsByClubNameOrClubEnglishName(String clubName, String clubEnglishName);

    Club getByClubEnglishName(String clubEnglishName);

    void validateClubExists(Long clubId);

    List<Club> getAll();

    Long count();
}
