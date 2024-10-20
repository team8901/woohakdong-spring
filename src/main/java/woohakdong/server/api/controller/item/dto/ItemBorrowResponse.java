package woohakdong.server.api.controller.item.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ItemBorrowResponse(
        Long itemId,
        Long itemHistoryId,
        LocalDateTime itemRentalDate,
        LocalDateTime itemDueDate
) {
}
