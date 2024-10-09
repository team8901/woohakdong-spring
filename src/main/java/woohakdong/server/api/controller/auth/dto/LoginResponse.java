package woohakdong.server.api.controller.auth.dto;

public record LoginResponse(
        String accessToken,
        String refreshToken
) {
}
