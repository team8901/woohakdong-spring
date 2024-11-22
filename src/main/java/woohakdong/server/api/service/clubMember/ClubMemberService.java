package woohakdong.server.api.service.clubMember;

import static woohakdong.server.common.exception.CustomErrorInfo.CLUB_MEMBER_ROLE_NOT_ALLOWED;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woohakdong.server.api.controller.clubMember.dto.ClubMemberInfoResponse;
import woohakdong.server.common.exception.CustomException;
import woohakdong.server.common.util.security.SecurityUtil;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.club.ClubRepository;
import woohakdong.server.domain.clubmember.ClubMember;
import woohakdong.server.domain.clubmember.ClubMemberRepository;
import woohakdong.server.domain.clubmember.ClubMemberRole;
import woohakdong.server.domain.member.Member;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ClubMemberService {

    private final SecurityUtil securityUtil;

    private final ClubMemberRepository clubMemberRepository;
    private final ClubRepository clubRepository;

    public List<ClubMemberInfoResponse> getMembers(Long clubId) {
        Club club = clubRepository.getById(clubId);
        LocalDate assignedTerm = getAssignedTerm();

        List<ClubMember> clubMembers = clubMemberRepository.getAllBySearchFilter(club, null, assignedTerm);

        return clubMembers.stream()
                .map(ClubMemberInfoResponse::from)
                .toList();
    }

    public ClubMemberInfoResponse getClubMemberInfo(Long clubId, Long clubMemberId) {
        Club club = clubRepository.getById(clubId);
        ClubMember clubMember = clubMemberRepository.getById(clubMemberId);

        return ClubMemberInfoResponse.from(clubMember);
    }

    public ClubMemberInfoResponse getMyInfo(Long clubId) {
        Member member = securityUtil.getMember();
        Club club = clubRepository.getById(clubId);
        LocalDate assignedTerm = getAssignedTerm();

        ClubMember clubMember = clubMemberRepository.getByClubAndMemberAndAssignedTerm(club, member, assignedTerm);

        return ClubMemberInfoResponse.from(clubMember);
    }

    @Transactional
    public void changeClubMemberRole(Long clubId, Long clubMemberId, ClubMemberRole clubMemberRole) {
        Member member = securityUtil.getMember();
        Club club = clubRepository.getById(clubId);
        ClubMember requestMember = clubMemberRepository.getByClubAndMember(club, member);

        if (!requestMember.getClubMemberRole().equals(ClubMemberRole.PRESIDENT)) {
            throw new CustomException(CLUB_MEMBER_ROLE_NOT_ALLOWED);
        }

        ClubMember clubMember = clubMemberRepository.getById(clubMemberId);
        clubMember.changeRole(clubMemberRole);
    }

    public List<ClubMemberInfoResponse> getFilteredMembers(Long clubId, String name, LocalDate assignedTerm) {
        assignedTerm = assignedTerm == null ? getAssignedTerm() : assignedTerm;
        Club club = clubRepository.getById(clubId);
        List<ClubMember> clubMemberList = clubMemberRepository.getAllBySearchFilter(club, name, assignedTerm);

        return clubMemberList.stream()
                .map(ClubMemberInfoResponse::from)
                .toList();
    }

    private LocalDate getAssignedTerm() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int semester = now.getMonthValue() <= 6 ? 1 : 7; // 1: 1학기, 7: 2학기
        return LocalDate.of(year, semester, 1);
    }
}
