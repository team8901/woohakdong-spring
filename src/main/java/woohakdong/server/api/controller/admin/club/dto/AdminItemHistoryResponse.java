package woohakdong.server.api.controller.admin.club.dto;

import lombok.Builder;
import woohakdong.server.api.controller.item.dto.ItemHistoryResponse;
import woohakdong.server.domain.ItemHistory.ItemHistory;
import woohakdong.server.domain.item.Item;
import woohakdong.server.domain.item.ItemCategory;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record AdminItemHistoryResponse(
        Long itemHistoryId,
        String memberName,
        LocalDateTime itemRentalDate,
        LocalDateTime itemDueDate,
        LocalDateTime itemReturnDate,
        String itemReturnImage,
        String itemName,
        ItemCategory itemCategory,
        Long itemId,
        LocalDate assignedTerm
) {
    public static AdminItemHistoryResponse from(ItemHistory itemHistory, Item item, LocalDate assignedTerm) {
        return AdminItemHistoryResponse.builder()
                .itemHistoryId(itemHistory.getItemHistoryId())
                .memberName(itemHistory.getMemberName())
                .itemRentalDate(itemHistory.getItemRentalDate())
                .itemDueDate(itemHistory.getItemDueDate())
                .itemReturnDate(itemHistory.getItemReturnDate())
                .itemReturnImage(itemHistory.getItemReturnImage())
                .itemName(item.getItemName())
                .itemCategory(item.getItemCategory())
                .itemId(item.getItemId())
                .assignedTerm(assignedTerm)
                .build();
    }
}
