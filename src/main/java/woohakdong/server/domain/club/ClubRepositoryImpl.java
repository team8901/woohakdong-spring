package woohakdong.server.domain.club;

import static woohakdong.server.common.exception.CustomErrorInfo.CLUB_NOT_FOUND;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import woohakdong.server.common.exception.CustomException;
import woohakdong.server.domain.school.School;

@RequiredArgsConstructor
@Repository
public class ClubRepositoryImpl implements ClubRepository{

    private final ClubJpaRepository clubJpaRepository;

    @Override
    public Club save(Club club) {
        return clubJpaRepository.save(club);
    }

    @Override
    public Boolean existsByClubNameOrClubEnglishName(String clubName, String clubEnglishName){
        return clubJpaRepository.existsByClubNameOrClubEnglishName(clubName, clubEnglishName);
    }

    @Override
    public Club getByClubEnglishName(String clubEnglishName) {
        return clubJpaRepository.findByClubEnglishName(clubEnglishName)
                .orElseThrow(() -> new CustomException(CLUB_NOT_FOUND));
    }

    @Override
    public void validateClubExists(Long clubId) {
        clubJpaRepository.findById(clubId)
                .orElseThrow(() -> new CustomException(CLUB_NOT_FOUND));
    }

    @Override
    public Club getById(Long clubId) {
        return clubJpaRepository.findById(clubId)
                .orElseThrow(() -> new CustomException(CLUB_NOT_FOUND));
    }

    @Override
    public List<Club> getAll() {
        return clubJpaRepository.findAll();
    }

    @Override
    public Long count() {
        return clubJpaRepository.count();
    }

    @Override
    public Long countBySchool(School school) {
        return clubJpaRepository.countBySchool(school);
    }

    @Override
    public List<Club> getBySchool(School school) {
        return clubJpaRepository.getBySchool(school);
    }
}
