package woohakdong.server.api.controller.admin.club.dto;

import lombok.Builder;
import woohakdong.server.api.controller.clubMember.dto.ClubMemberInfoResponse;
import woohakdong.server.domain.clubmember.ClubMember;
import woohakdong.server.domain.clubmember.ClubMemberRole;
import woohakdong.server.domain.member.Member;
import woohakdong.server.domain.member.MemberGender;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record ClubMemberResponse(
        Long memberId,
        String memberName,
        String memberPhoneNumber,
        String memberEmail,
        MemberGender memberGender,
        String memberMajor,
        String memberStudentNumber,
        ClubMemberRole clubMemberRole,
        Long clubMemberId,
        LocalDate clubJoinedDate,
        LocalDate clubMemberAssignedTerm,
        LocalDateTime createAt
) {
    public static ClubMemberResponse from(ClubMember clubMember) {
        Member member = clubMember.getMember();
        return ClubMemberResponse.builder()
                .memberId(member.getMemberId())
                .memberName(member.getMemberName())
                .memberPhoneNumber(member.getMemberPhoneNumber())
                .memberEmail(member.getMemberEmail())
                .memberMajor(member.getMemberMajor())
                .memberStudentNumber(member.getMemberStudentNumber())
                .memberGender(member.getMemberGender())
                .clubMemberId(clubMember.getClubMemberId())
                .clubMemberRole(clubMember.getClubMemberRole())
                .clubJoinedDate(clubMember.getClubJoinedDate())
                .clubMemberAssignedTerm(clubMember.getClubMemberAssignedTerm())
                .createAt(clubMember.getCreatedAt())
                .build();
    }
}
