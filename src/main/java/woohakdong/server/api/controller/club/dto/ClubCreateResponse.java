package woohakdong.server.api.controller.club.dto;

import lombok.Builder;

@Builder
public record ClubCreateResponse(
        Long clubId
) {
}
