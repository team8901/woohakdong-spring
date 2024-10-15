package woohakdong.server.api.controller.group.dto;

import lombok.Builder;

@Builder
public record ClubJoinOrderResponse(
        Long orderId
) {
}
