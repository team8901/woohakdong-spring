package woohakdong.server.api.controller.item.dto;

import lombok.Builder;
import woohakdong.server.domain.item.ItemCategory;

@Builder
public record ItemUpdateRequest(
        String itemName,
        String itemPhoto,
        String itemDescription,
        String itemLocation,
        ItemCategory itemCategory,
        Integer itemRentalMaxDay
) {
}
