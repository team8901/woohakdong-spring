package woohakdong.server.api.controller.admin.auth.dto;

public record AdminInfoUpdateRequest(
        String memberLoginId,
        String memberName,
        String memberEmail,
        String memberPassword
) {
}
