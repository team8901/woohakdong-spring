package woohakdong.server.api.controller.item.dto;

import lombok.Builder;

@Builder
public record ItemRegisterResponse(
        Long itemId,
        String itemName
) {
}
