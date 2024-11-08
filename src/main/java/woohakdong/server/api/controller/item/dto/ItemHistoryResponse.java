package woohakdong.server.api.controller.item.dto;

import lombok.Builder;
import woohakdong.server.domain.ItemHistory.ItemHistory;

import java.time.LocalDateTime;

@Builder
public record ItemHistoryResponse(
        Long itemHistoryId,
        Long memberId,
        String memberName,
        LocalDateTime itemRentalDate,
        LocalDateTime itemDueDate,
        LocalDateTime itemReturnDate,
        String itemReturnImage
) {
    public static ItemHistoryResponse from(ItemHistory itemHistory) {
        return new ItemHistoryResponse(
                itemHistory.getItemHistoryId(),
                itemHistory.getItem().getItemId(),
                itemHistory.getItem().getItemName(),
                itemHistory.getItemRentalDate(),
                itemHistory.getItemReturnDate(),
                itemHistory.getItemDueDate(),
                itemHistory.getItemReturnImage()
        );
    }
}
