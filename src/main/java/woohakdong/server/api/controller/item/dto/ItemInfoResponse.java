package woohakdong.server.api.controller.item.dto;

import lombok.Builder;
import woohakdong.server.domain.item.Item;
import woohakdong.server.domain.item.ItemCategory;

import java.time.LocalDateTime;

@Builder
public record ItemInfoResponse(
        Long itemId,
        String itemName,
        String itemPhoto,
        String itemDescription,
        String itemLocation,
        ItemCategory itemCategory,
        Integer itemRentalMaxDay,
        Boolean itemAvailable,
        Boolean itemUsing,
        LocalDateTime itemRentalDate,
        Integer itemRentalTime
) {
    public static ItemInfoResponse of(Item item) {
        return ItemInfoResponse.builder()
                .itemId(item.getItemId())
                .itemName(item.getItemName())
                .itemPhoto(item.getItemPhoto())
                .itemDescription(item.getItemDescription())
                .itemLocation(item.getItemLocation())
                .itemCategory(item.getItemCategory())
                .itemRentalMaxDay(item.getItemRentalMaxDay())
                .itemAvailable(item.getItemAvailable())
                .itemUsing(item.getItemUsing())
                .itemRentalDate(item.getItemRentalDate())
                .itemRentalTime(item.getItemRentalTime())
                .build();
    }
}
