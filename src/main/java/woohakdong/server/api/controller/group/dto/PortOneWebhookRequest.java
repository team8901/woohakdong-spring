package woohakdong.server.api.controller.group.dto;

public record PortOneWebhookRequest(
        String merchantUid,
        String impUid,
        String status
) {
}
