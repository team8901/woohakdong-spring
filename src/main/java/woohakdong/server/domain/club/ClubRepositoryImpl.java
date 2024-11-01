package woohakdong.server.domain.club;

import static woohakdong.server.common.exception.CustomErrorInfo.CLUB_NOT_FOUND;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import woohakdong.server.common.exception.CustomException;

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
    public Club getById(Long clubId) {
        return clubJpaRepository.findById(clubId)
                .orElseThrow(() -> new CustomException(CLUB_NOT_FOUND));
    }
}
