package woohakdong.server.api.controller.item.dto;

import lombok.Builder;
import woohakdong.server.domain.item.Item;

@Builder
public record ItemRegisterResponse(
        Long itemId
) {
    public static ItemRegisterResponse of(Item item) {
        return ItemRegisterResponse.builder()
                .itemId(item.getItemId())
                .build();
    }
}
