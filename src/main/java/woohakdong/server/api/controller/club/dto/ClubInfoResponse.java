package woohakdong.server.api.controller.club.dto;

public record ClubInfoResponse(
        Long clubId,
        String clubName,
        String clubNameEnglish,
        String clubImage,
        String clubDescription,
        String clubRoom,
        String clubGeneration
) {
}
