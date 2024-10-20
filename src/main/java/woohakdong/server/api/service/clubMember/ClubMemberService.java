package woohakdong.server.api.service.clubMember;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woohakdong.server.api.controller.clubMember.dto.ClubMemberInfoResponse;
import woohakdong.server.common.exception.CustomException;
import woohakdong.server.domain.club.ClubRepository;
import woohakdong.server.domain.clubmember.ClubMember;
import woohakdong.server.domain.clubmember.ClubMemberRepository;
import woohakdong.server.domain.member.Member;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static woohakdong.server.common.exception.CustomErrorInfo.CLUB_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ClubMemberService {

    private final ClubMemberRepository clubMemberRepository;

    public List<ClubMemberInfoResponse> getMembers(Long clubId) {
        LocalDate assignedTerm = getAssignedTerm();

        // clubId와 assignedTerm으로 클럽 멤버 조회
        List<ClubMember> clubMembers = clubMemberRepository.findByClubIdAndAssignedTerm(clubId, assignedTerm);

        // ClubMember를 ClubMemberInfoResponse로 변환하여 반환
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
                    .clubMemberAssignedTerm(clubMember.getClubMemberAssignedTerm())
                    .build();
        }).collect(Collectors.toList());
    }

    public List<ClubMemberInfoResponse> getTermMembers(Long clubId, LocalDate clubMemberAssignedTerm) {

        // clubId와 assignedTerm으로 클럽 멤버 조회
        List<ClubMember> clubMembers = clubMemberRepository.findByClubIdAndAssignedTerm(clubId, clubMemberAssignedTerm);

        // ClubMember를 ClubMemberInfoResponse로 변환하여 반환
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
