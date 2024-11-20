package woohakdong.server.api.controller.club.dto;

import lombok.Builder;
import woohakdong.server.domain.club.Club;

@Builder
public record ClubSummaryResponse(
        Long clubId,
        String clubName,
        String clubEnglishName,
        String clubImage,
        String clubDescription,
        String clubRoom,
        String clubGeneration,
        Integer clubDues
) {
    public static ClubSummaryResponse from(Club club) {
        return ClubSummaryResponse.builder()
                .clubId(club.getClubId())
                .clubName(club.getClubName())
                .clubEnglishName(club.getClubEnglishName())
                .clubImage(club.getClubImage())
                .clubDescription(club.getClubDescription())
                .clubRoom(club.getClubRoom())
                .clubGeneration(club.getClubGeneration())
                .clubDues(club.getClubDues())
                .build();
    }
}
