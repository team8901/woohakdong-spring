package woohakdong.server.api.controller.group.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record PortOneWebhookRequest(
        @NotNull @JsonProperty("merchant_uid")
        String merchantUid,
        @NotNull @JsonProperty("imp_uid")
        String impUid,
        @NotNull
        String status
) {
}
