package woohakdong.server.api.controller.member.dto;

import lombok.Builder;
import woohakdong.server.domain.member.MemberGender;

@Builder
public record MemberInfoResponse(
        String memberName,
        String memberPhoneNumber,
        String memberEmail,
        String memberSchool,
        String memberMajor,
        String memberStudentNumber,
        MemberGender memberGender
) {
}
