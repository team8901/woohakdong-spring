package woohakdong.server.api.service.clubMember;

import static woohakdong.server.common.exception.CustomErrorInfo.CLUB_MEMBER_ROLE_NOT_ALLOWED;
import static woohakdong.server.common.exception.CustomErrorInfo.MEMBER_NOT_FOUND;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woohakdong.server.api.controller.clubMember.dto.ClubMemberInfoResponse;
import woohakdong.server.common.exception.CustomException;
import woohakdong.server.common.security.jwt.CustomUserDetails;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.club.ClubRepository;
import woohakdong.server.domain.clubmember.ClubMember;
import woohakdong.server.domain.clubmember.ClubMemberRepository;
import woohakdong.server.domain.clubmember.ClubMemberRole;
import woohakdong.server.domain.member.Member;
import woohakdong.server.domain.member.MemberRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ClubMemberService {

    private final ClubMemberRepository clubMemberRepository;
    private final ClubRepository clubRepository;
    private final MemberRepository memberRepository;

    public List<ClubMemberInfoResponse> getMembers(Long clubId) {
        Club club = clubRepository.getById(clubId);
        LocalDate assignedTerm = getAssignedTerm();

        List<ClubMember> clubMembers = clubMemberRepository.getByClubAndAssignedTerm(club, assignedTerm);

        return clubMembers.stream()
                .map(clubMember -> ClubMemberInfoResponse.from(clubMember.getMember(), clubMember))
                .toList();
    }

    public List<ClubMemberInfoResponse> getTermMembers(Long clubId, LocalDate clubMemberAssignedTerm) {
        Club club = clubRepository.getById(clubId);
        List<ClubMember> clubMembers = clubMemberRepository.getByClubAndAssignedTerm(club, clubMemberAssignedTerm);

        return clubMembers.stream()
                .map(clubMember -> ClubMemberInfoResponse.from(clubMember.getMember(), clubMember))
                .toList();
    }

    public ClubMemberInfoResponse getClubMemberInfo(Long clubId, Long clubMemberId) {
        Club club = clubRepository.getById(clubId);

        ClubMember clubMember = clubMemberRepository.getById(clubMemberId);

        return ClubMemberInfoResponse.from(clubMember.getMember(), clubMember);
    }

    public ClubMemberInfoResponse getMyInfo(Long clubId) {
        Member member = getMemberFromJwtInformation();
        Club club = clubRepository.getById(clubId);
        LocalDate assignedTerm = getAssignedTerm();

        ClubMember clubMember = clubMemberRepository.getByClubAndMemberAndAssignedTerm(club, member, assignedTerm);

        return ClubMemberInfoResponse.from(clubMember.getMember(), clubMember);
    }

    @Transactional
    public void changeClubMemberRole(Long clubId, Long clubMemberId, ClubMemberRole clubMemberRole) {
        Member member = getMemberFromJwtInformation();
        Club club = clubRepository.getById(clubId);
        ClubMember requestMember = clubMemberRepository.getByClubAndMember(club, member);

        if (!requestMember.getClubMemberRole().equals(ClubMemberRole.PRESIDENT)) {
            throw new CustomException(CLUB_MEMBER_ROLE_NOT_ALLOWED);
        }

        ClubMember clubMember = clubMemberRepository.getById(clubMemberId);
        clubMember.changeRole(clubMemberRole);
    }

    private LocalDate getAssignedTerm() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int semester = now.getMonthValue() <= 6 ? 1 : 7; // 1: 1학기, 7: 2학기
        return LocalDate.of(year, semester, 1);
    }

    private Member getMemberFromJwtInformation() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String provideId = userDetails.getUsername();

        return memberRepository.findByMemberProvideId(provideId)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
    }
}
