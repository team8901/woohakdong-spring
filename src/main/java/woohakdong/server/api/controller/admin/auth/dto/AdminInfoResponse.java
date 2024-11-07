package woohakdong.server.api.controller.admin.auth.dto;

import lombok.Builder;
import woohakdong.server.domain.member.Member;

@Builder
public record AdminInfoResponse(
        String memberLoginId,
        String memberName,
        String memberEmail
) {
    public static AdminInfoResponse from(Member admin) {
        return AdminInfoResponse.builder()
                .memberLoginId(admin.getMemberProvideId())
                .memberName(admin.getMemberName())
                .memberEmail(admin.getMemberEmail())
                .build();
    }
}
