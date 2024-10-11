package woohakdong.server.api.service.club;

import static woohakdong.server.common.exception.CustomErrorInfo.CLUB_NAME_DUPLICATION;
import static woohakdong.server.common.exception.CustomErrorInfo.MEMBER_NOT_FOUND;
import static woohakdong.server.common.exception.CustomErrorInfo.SCHOOL_NOT_FOUND;
import static woohakdong.server.domain.gathering.GatheringType.JOIN;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woohakdong.server.api.controller.club.dto.ClubCreateRequest;
import woohakdong.server.api.controller.club.dto.ClubCreateResponse;
import woohakdong.server.common.exception.CustomException;
import woohakdong.server.common.security.jwt.CustomUserDetails;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.club.ClubRepository;
import woohakdong.server.domain.gathering.Gathering;
import woohakdong.server.domain.member.Member;
import woohakdong.server.domain.member.MemberRepository;
import woohakdong.server.domain.school.School;
import woohakdong.server.domain.school.SchoolRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ClubService {

    private final ClubRepository clubRepository;
    private final MemberRepository memberRepository;
    private final SchoolRepository schoolRepository;

    public void validateClubWithNames(String clubName, String clubEnglishName) {
        if (clubRepository.existsByClubNameOrClubEnglishName(clubName, clubEnglishName)) {
            throw new CustomException(CLUB_NAME_DUPLICATION);
        }
    }

    @Transactional
    public ClubCreateResponse registerClub(ClubCreateRequest clubCreateRequest) {
        Member member = getMemberFromJwtInformation();
        School school = schoolRepository.findById(member.getSchool().getSchoolId())
                .orElseThrow(() -> new CustomException(SCHOOL_NOT_FOUND));

        Club club = createClub(clubCreateRequest, school);
        club.addGathering(createJoinGathering(clubCreateRequest, club));
        clubRepository.save(club);

        return ClubCreateResponse.builder()
                .clubId(club.getClubId())
                .build();
    }

    private Club createClub(ClubCreateRequest clubCreateRequest, School school) {
        validateClubWithNames(clubCreateRequest.clubName(), clubCreateRequest.clubEnglishName());
        return Club.builder()
                .clubName(clubCreateRequest.clubName())
                .clubEnglishName(clubCreateRequest.clubEnglishName())
                .clubImage(clubCreateRequest.clubImage())
                .clubDescription(clubCreateRequest.clubDescription())
                .clubRoom(clubCreateRequest.clubRoom())
                .school(school)
                .build();
    }

    private Gathering createJoinGathering(ClubCreateRequest clubCreateRequest, Club club) {
        return Gathering.builder()
                .gatheringLink("https://woohakdong.com/clubs/" + club.getClubEnglishName())
                .club(club)
                .gatheringAmount(clubCreateRequest.clubDues())
                .gatheringType(JOIN)
                .gatheringName(clubCreateRequest.clubGeneration() + "기 모집")
                .build();
    }

    private Member getMemberFromJwtInformation() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String provideId = userDetails.getUsername();

        return memberRepository.findByMemberProvideId(provideId)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
    }
}
