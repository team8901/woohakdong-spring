package woohakdong.server.api.controller.group.dto;

import lombok.Builder;

@Builder
public record ClubJoinOrderRequest(
        String merchantUid
) {
}
