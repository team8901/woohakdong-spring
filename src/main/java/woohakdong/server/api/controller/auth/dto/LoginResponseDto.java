package woohakdong.server.api.controller.auth.dto;

public record LoginResponseDto(String accessToken, String refreshToken) {
}
