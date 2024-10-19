package woohakdong.server.api.controller.item.dto;

import lombok.Builder;
import woohakdong.server.domain.item.ItemCategory;

import java.time.LocalDateTime;

@Builder
public record ItemListResponse(
        Long itemId,
        String itemName,
        String itemPhoto,
        String itemDescription,
        String itemLocation,
        ItemCategory itemCategory,
        Integer itemRentalMaxDay,
        Boolean itemAvailable,
        Boolean itemUsing,
        LocalDateTime itemRentalDate
) {
}
