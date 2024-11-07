package woohakdong.server.api.controller.member.dto;

import lombok.Builder;
import woohakdong.server.domain.member.Member;
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
    public static MemberInfoResponse from(Member member) {
        return MemberInfoResponse.builder()
                .memberName(member.getMemberName())
                .memberPhoneNumber(member.getMemberPhoneNumber())
                .memberEmail(member.getMemberEmail())
                .memberSchool(member.getSchool().getSchoolName())
                .memberMajor(member.getMemberMajor())
                .memberStudentNumber(member.getMemberStudentNumber())
                .memberGender(member.getMemberGender())
                .build();
    }
}
