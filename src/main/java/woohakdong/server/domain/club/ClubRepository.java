package woohakdong.server.domain.club;

public interface ClubRepository {
    Club save(Club club);

    Club getById(Long clubId);

    Boolean existsByClubNameOrClubEnglishName(String clubName, String clubEnglishName);

    Club getByClubEnglishName(String clubEnglishName);

    void validateClubExists(Long clubId);
}
