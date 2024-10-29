package woohakdong.server.api.controller.item.dto;

import jakarta.validation.constraints.NotNull;
import woohakdong.server.domain.item.ItemCategory;

public record ItemUpdateRequest(
        String itemName,
        String itemPhoto,
        String itemDescription,
        String itemLocation,
        ItemCategory itemCategory,
        Integer itemRentalMaxDay
) {
}
