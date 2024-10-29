package woohakdong.server.api.controller.clubMember.dto;

import lombok.Builder;
import woohakdong.server.domain.member.MemberGender;

import java.time.LocalDate;

@Builder
public record ClubMemberInfoResponse(
        Long memberId,
        String memberName,
        String memberPhoneNumber,
        String memberEmail,
        String memberSchool,
        MemberGender memberGender,
        String memberMajor,
        String memberStudentNumber,
        String clubMemberRole,
        LocalDate clubJoinedDate,
        LocalDate clubMemberAssignedTerm
) {

}
