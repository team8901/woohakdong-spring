package woohakdong.server.api.controller.club.dto;

public record ClubAccountRegisterRequest(
        String accountName,
        String accountNumber
) {
}
