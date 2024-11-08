package woohakdong.server.api.controller.item.dto;

import lombok.Builder;
import lombok.Getter;
import woohakdong.server.domain.item.Item;
import woohakdong.server.domain.item.ItemCategory;

import java.time.LocalDateTime;

@Builder
public record ItemBorrowedResponse(
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
        Integer itemRentalTime,
        LocalDateTime itemBorrowedReturnDate
) {
    public static ItemBorrowedResponse from(Item item, LocalDateTime borrowedReturnDate) {
        return ItemBorrowedResponse.builder()
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
                .itemBorrowedReturnDate(borrowedReturnDate)
                .build();
    }
}
