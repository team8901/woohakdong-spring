package woohakdong.server.api.service.club;

import static woohakdong.server.common.exception.CustomErrorInfo.CLUB_MEMBER_ROLE_NOT_ALLOWED;
import static woohakdong.server.common.exception.CustomErrorInfo.CLUB_NAME_DUPLICATION;
import static woohakdong.server.common.exception.CustomErrorInfo.CLUB_NOT_FOUND;
import static woohakdong.server.common.exception.CustomErrorInfo.MEMBER_NOT_FOUND;
import static woohakdong.server.common.exception.CustomErrorInfo.SCHOOL_NOT_FOUND;
import static woohakdong.server.domain.clubmember.ClubMemberRole.PRESIDENT;
import static woohakdong.server.domain.gathering.GatheringType.JOIN;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woohakdong.server.api.controller.club.dto.ClubAccountRegisterRequest;
import woohakdong.server.api.controller.club.dto.ClubCreateRequest;
import woohakdong.server.api.controller.club.dto.ClubCreateResponse;
import woohakdong.server.api.controller.club.dto.ClubInfoResponse;
import woohakdong.server.common.exception.CustomException;
import woohakdong.server.common.security.jwt.CustomUserDetails;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.club.ClubRepository;
import woohakdong.server.domain.clubAccount.ClubAccount;
import woohakdong.server.domain.clubAccount.ClubAccountRepository;
import woohakdong.server.domain.clubmember.ClubMember;
import woohakdong.server.domain.clubmember.ClubMemberRepository;
import woohakdong.server.domain.clubmember.ClubMemberRole;
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
    private final ClubAccountRepository clubAccountRepository;
    private final ClubMemberRepository clubMemberRepository;

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

        ClubMember clubMember = createClubMember(member, club, PRESIDENT);
        club.addClubMember(clubMember);

        clubRepository.save(club);

        return ClubCreateResponse.builder()
                .clubId(club.getClubId())
                .build();
    }

    @Transactional
    public void registerClubAccount(Long clubId, ClubAccountRegisterRequest clubAccountRegisterRequest) {
        Member member = getMemberFromJwtInformation();
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new CustomException(CLUB_NOT_FOUND));

        if (!clubMemberRepository.existsByClubAndMemberAndClubMemberRole(club, member, PRESIDENT)) {
            throw new CustomException(CLUB_MEMBER_ROLE_NOT_ALLOWED);
        }

        ClubAccount clubAccount = createClubAccount(clubAccountRegisterRequest, club);
        clubAccountRepository.save(clubAccount);
    }

    public ClubInfoResponse findClubInfo(Long clubId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new CustomException(CLUB_NOT_FOUND));

        return ClubInfoResponse.from(club);
    }


    private Club createClub(ClubCreateRequest clubCreateRequest, School school) {
        validateClubWithNames(clubCreateRequest.clubName(), clubCreateRequest.clubEnglishName());
        return Club.builder()
                .clubName(clubCreateRequest.clubName())
                .clubEnglishName(clubCreateRequest.clubEnglishName())
                .clubImage(clubCreateRequest.clubImage())
                .clubDescription(clubCreateRequest.clubDescription())
                .clubRoom(clubCreateRequest.clubRoom())
                .clubGeneration(clubCreateRequest.clubGeneration())
                .school(school)
                .build();
    }

    private Gathering createJoinGathering(ClubCreateRequest clubCreateRequest, Club club) {
        return Gathering.builder()
                .gatheringLink("https://woohakdong.com/clubs/" + club.getClubEnglishName())
                .club(club)
                .gatheringAmount(clubCreateRequest.clubDues())
                .gatheringType(JOIN)
                .gatheringName(club.getClubGeneration() + "기 모집")
                .build();
    }

    private Member getMemberFromJwtInformation() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String provideId = userDetails.getUsername();

        return memberRepository.findByMemberProvideId(provideId)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
    }

    private ClubAccount createClubAccount(ClubAccountRegisterRequest clubAccountRegisterRequest, Club club) {
        return ClubAccount.builder()
                .clubAccountBankName(clubAccountRegisterRequest.clubAccountBankName())
                .clubAccountNumber(clubAccountRegisterRequest.clubAccountNumber())
                .clubAccountPinTechNumber(clubAccountRegisterRequest.clubAccountPinTechNumber())
                .club(club)
                .build();
    }

    private ClubMember createClubMember(Member member, Club club, ClubMemberRole role) {
        return ClubMember.builder()
                .clubMemberRole(role)
                .member(member)
                .club(club)
                .clubMemberAssignedTerm(getAssignedTerm())
                .build();
    }

    private LocalDate getAssignedTerm() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int semester = now.getMonthValue() <= 6 ? 1 : 7; // 1: 1학기, 7: 2학기
        return LocalDate.of(year, semester, 1);
    }
}
