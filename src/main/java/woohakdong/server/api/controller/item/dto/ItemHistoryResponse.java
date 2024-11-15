package woohakdong.server.api.controller.item.dto;

import lombok.Builder;
import woohakdong.server.domain.ItemHistory.ItemHistory;
import woohakdong.server.domain.member.Member;

import java.time.LocalDateTime;

@Builder
public record ItemHistoryResponse(
        Long itemHistoryId,
        Long clubMemberId,
        String memberName,
        LocalDateTime itemRentalDate,
        LocalDateTime itemDueDate,
        LocalDateTime itemReturnDate,
        String itemReturnImage
) {
    public static ItemHistoryResponse from(ItemHistory itemHistory, Long clubMemberId) {
        return ItemHistoryResponse.builder()
                .itemHistoryId(itemHistory.getItemHistoryId())
                .clubMemberId(clubMemberId)
                .memberName(itemHistory.getMemberName())
                .itemRentalDate(itemHistory.getItemRentalDate())
                .itemDueDate(itemHistory.getItemDueDate())
                .itemReturnDate(itemHistory.getItemReturnDate())
                .itemReturnImage(itemHistory.getItemReturnImage())
                .build();
    }
}
