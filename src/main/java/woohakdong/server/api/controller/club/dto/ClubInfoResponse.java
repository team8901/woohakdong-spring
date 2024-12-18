package woohakdong.server.api.controller.club.dto;

import lombok.Builder;
import woohakdong.server.domain.club.Club;

@Builder
public record ClubInfoResponse(
        Long clubId,
        String clubName,
        String clubEnglishName,
        String clubImage,
        String clubDescription,
        String clubRoom,
        String clubGeneration,
        String clubGroupChatLink,
        String clubGroupChatPassword,
        Integer clubDues
) {
    public static ClubInfoResponse from(Club club) {
        return ClubInfoResponse.builder()
                .clubId(club.getClubId())
                .clubName(club.getClubName())
                .clubEnglishName(club.getClubEnglishName())
                .clubImage(club.getClubImage())
                .clubDescription(club.getClubDescription())
                .clubRoom(club.getClubRoom())
                .clubGeneration(club.getClubGeneration())
                .clubGroupChatLink(club.getClubGroupChatLink())
                .clubGroupChatPassword(club.getClubGroupChatPassword())
                .clubDues(club.getClubDues())
                .build();
    }
}
