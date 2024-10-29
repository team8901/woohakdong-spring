package woohakdong.server.api.controller.item.dto;

import jakarta.validation.constraints.NotNull;

public record ItemAvailableUpdateRequest(
        @NotNull
        Boolean itemAvailable
) {
}
