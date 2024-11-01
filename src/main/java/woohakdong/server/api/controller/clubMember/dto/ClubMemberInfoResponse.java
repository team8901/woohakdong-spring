package woohakdong.server.api.controller.clubMember.dto;

import lombok.Builder;
import woohakdong.server.domain.clubmember.ClubMember;
import woohakdong.server.domain.clubmember.ClubMemberRole;
import woohakdong.server.domain.member.Member;
import woohakdong.server.domain.member.MemberGender;

import java.time.LocalDate;

@Builder
public record ClubMemberInfoResponse(
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
        LocalDate clubMemberAssignedTerm
) {
    public static ClubMemberInfoResponse from(Member member, ClubMember clubMember) {
        return ClubMemberInfoResponse.builder()
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
                .build();
    }

}
