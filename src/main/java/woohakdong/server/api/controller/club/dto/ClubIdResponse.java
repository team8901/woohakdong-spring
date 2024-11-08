package woohakdong.server.api.controller.club.dto;

import lombok.Builder;
import woohakdong.server.domain.club.Club;

@Builder
public record ClubIdResponse(
        Long clubId
) {

    public static ClubIdResponse from(Club club) {
        return ClubIdResponse.builder()
                .clubId(club.getClubId())
                .build();
    }
}
