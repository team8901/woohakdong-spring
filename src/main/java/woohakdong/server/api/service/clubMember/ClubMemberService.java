package woohakdong.server.api.service.clubMember;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woohakdong.server.api.controller.clubMember.dto.ClubMemberInfoResponse;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.club.ClubRepository;
import woohakdong.server.domain.clubmember.ClubMember;
import woohakdong.server.domain.clubmember.ClubMemberRepository;
import woohakdong.server.domain.member.Member;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ClubMemberService {

    private final ClubMemberRepository clubMemberRepository;
    private final ClubRepository clubRepository;

    public List<ClubMemberInfoResponse> getMembers(Long clubId) {
        Club club = clubRepository.getById(clubId);
        LocalDate assignedTerm = getAssignedTerm();

        List<ClubMember> clubMembers = clubMemberRepository.getByClubIdAndAssignedTerm(club, assignedTerm);

        return clubMembers.stream().map(clubMember -> {
            Member member = clubMember.getMember();
            return ClubMemberInfoResponse.builder()
                    .memberId(member.getMemberId())
                    .memberName(member.getMemberName())
                    .memberPhoneNumber(member.getMemberPhoneNumber())
                    .memberEmail(member.getMemberEmail())
                    .memberSchool(member.getSchool().getSchoolName())
                    .memberMajor(member.getMemberMajor())
                    .memberStudentNumber(member.getMemberStudentNumber())
                    .memberGender(member.getMemberGender())
                    .clubMemberRole(clubMember.getClubMemberRole().name())
                    .clubJoinedDate(clubMember.getClubJoinedDate())
                    .clubMemberAssignedTerm(clubMember.getClubMemberAssignedTerm())
                    .build();
        }).collect(Collectors.toList());
    }

    public List<ClubMemberInfoResponse> getTermMembers(Long clubId, LocalDate clubMemberAssignedTerm) {
        Club club = clubRepository.getById(clubId);
        List<ClubMember> clubMembers = clubMemberRepository.getByClubIdAndAssignedTerm(club, clubMemberAssignedTerm);

        return clubMembers.stream().map(clubMember -> {
            Member member = clubMember.getMember();
            return ClubMemberInfoResponse.builder()
                    .memberId(member.getMemberId())
                    .memberName(member.getMemberName())
                    .memberPhoneNumber(member.getMemberPhoneNumber())
                    .memberEmail(member.getMemberEmail())
                    .memberSchool(member.getSchool().getSchoolName())
                    .memberMajor(member.getMemberMajor())
                    .memberStudentNumber(member.getMemberStudentNumber())
                    .memberGender(member.getMemberGender())
                    .clubMemberRole(clubMember.getClubMemberRole().name())
                    .clubJoinedDate(clubMember.getClubJoinedDate())
                    .clubMemberAssignedTerm(clubMember.getClubMemberAssignedTerm())
                    .build();
        }).collect(Collectors.toList());
    }

    private LocalDate getAssignedTerm() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int semester = now.getMonthValue() <= 6 ? 1 : 7; // 1: 1학기, 7: 2학기
        return LocalDate.of(year, semester, 1);
    }
}
