package woohakdong.server.api.service.clubMember;

import static woohakdong.server.common.exception.CustomErrorInfo.CLUB_MEMBER_ROLE_NOT_ALLOWED;
import static woohakdong.server.domain.clubmember.ClubMemberRole.MEMBER;
import static woohakdong.server.domain.clubmember.ClubMemberRole.OFFICER;
import static woohakdong.server.domain.clubmember.ClubMemberRole.PRESIDENT;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woohakdong.server.api.controller.SliceResponse;
import woohakdong.server.api.controller.clubMember.dto.ClubMemberInfoResponse;
import woohakdong.server.common.exception.CustomException;
import woohakdong.server.common.util.date.DateUtil;
import woohakdong.server.common.util.security.SecurityUtil;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.club.ClubRepository;
import woohakdong.server.domain.clubAccount.ClubAccount;
import woohakdong.server.domain.clubAccount.ClubAccountRepository;
import woohakdong.server.domain.clubAccountHistory.ClubAccountHistory;
import woohakdong.server.domain.clubAccountHistory.ClubAccountHistoryRepository;
import woohakdong.server.domain.clubmember.ClubMember;
import woohakdong.server.domain.clubmember.ClubMemberRepository;
import woohakdong.server.domain.clubmember.ClubMemberRole;
import woohakdong.server.domain.member.Member;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ClubMemberService {

    private final SecurityUtil securityUtil;
    private final DateUtil dateUtil;

    private final ClubMemberRepository clubMemberRepository;
    private final ClubRepository clubRepository;
    private final ClubAccountRepository clubAccountRepository;
    private final ClubAccountHistoryRepository clubAccountHistoryRepository;

    public List<ClubMemberInfoResponse> getMembers(Long clubId, LocalDate date) {
        Club club = clubRepository.getById(clubId);
        LocalDate assignedTerm = dateUtil.getAssignedTerm(date);

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

    public ClubMemberInfoResponse getMyInfo(Long clubId, LocalDate date) {
        Member member = securityUtil.getMember();
        Club club = clubRepository.getById(clubId);
        LocalDate assignedTerm = dateUtil.getAssignedTerm(date);

        ClubMember clubMember = clubMemberRepository.getByClubAndMemberAndAssignedTerm(club, member, assignedTerm);

        return ClubMemberInfoResponse.from(clubMember);
    }

    @Transactional
    public void changeClubMemberRole(Long clubId, Long clubMemberId, ClubMemberRole clubMemberRole, LocalDate date) {
        Member member = securityUtil.getMember();
        Club club = clubRepository.getById(clubId);
        LocalDate assignedTerm = dateUtil.getAssignedTerm(date);
        ClubMember president = clubMemberRepository.getByClubAndMemberAndAssignedTerm(club, member, assignedTerm);

        if (!president.getClubMemberRole().equals(PRESIDENT)) {
            throw new CustomException(CLUB_MEMBER_ROLE_NOT_ALLOWED);
        }

        ClubMember clubMember = clubMemberRepository.getById(clubMemberId);
        clubMember.changeRole(clubMemberRole);
    }

    public Slice<ClubMemberInfoResponse> getFilteredMembers(Long clubId, String name, LocalDate date, Pageable pageable) {
        LocalDate assignedTerm = dateUtil.getAssignedTerm(date);
        Club club = clubRepository.getById(clubId);
        Slice<ClubMember> clubMemberList = clubMemberRepository.getAllBySearchFilterPaging(club, name, assignedTerm, pageable);

        return clubMemberList.map(ClubMemberInfoResponse::from);
    }

    @Transactional
    public void passOnThePresidency(Long clubId, Long clubMemberId, LocalDate date) {
        Member member = securityUtil.getMember();
        Club club = clubRepository.getById(clubId);
        LocalDate assignedTerm = dateUtil.getAssignedTerm(date);
        ClubMember president = clubMemberRepository.getByClubAndMemberAndAssignedTerm(club, member, assignedTerm);
        president.hasAuthorityOf(PRESIDENT);

        ClubMember clubMember = clubMemberRepository.getById(clubMemberId);
        clubMember.changeRole(PRESIDENT);
        president.changeRole(OFFICER);

        ClubAccount clubAccount = clubAccountRepository.getByClub(club);
        clubAccountHistoryRepository.deleteAllByClubAccount(clubAccount);
        clubAccountRepository.delete(clubAccount);
    }
}
