package woohakdong.server.api.controller.group.dto;

import lombok.Builder;

@Builder
public record PortOneWebhookRequest(
        String merchantUid,
        String impUid,
        String status
) {
}
