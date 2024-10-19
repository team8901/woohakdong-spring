package woohakdong.server.api.controller.item.dto;

import jakarta.validation.constraints.NotNull;
import woohakdong.server.domain.item.ItemCategory;

public record ItemRegisterRequest(
        @NotNull
        String itemName,
        @NotNull
        String itemPhoto,
        @NotNull
        String itemDescription,
        @NotNull
        String itemLocation,
        @NotNull
        ItemCategory itemCategory,
        @NotNull
        Integer itemRentalMaxDay
) {
}
