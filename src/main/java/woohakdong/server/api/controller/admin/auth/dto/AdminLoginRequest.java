package woohakdong.server.api.controller.admin.auth.dto;

public record AdminLoginRequest(
        String memberLoginId,
        String memberPassword
) {
}
