package woohakdong.server.api.controller.item.dto;

import lombok.Builder;
import woohakdong.server.domain.ItemHistory.ItemHistory;
import woohakdong.server.domain.member.Member;

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
    public static ItemHistoryResponse from(ItemHistory itemHistory, Member member) {
        return ItemHistoryResponse.builder()
                .itemHistoryId(itemHistory.getItemHistoryId())
                .memberId(member.getMemberId())
                .memberName(member.getMemberName())
                .itemRentalDate(itemHistory.getItemRentalDate())
                .itemDueDate(itemHistory.getItemDueDate())
                .itemReturnDate(itemHistory.getItemReturnDate())
                .itemReturnImage(itemHistory.getItemReturnImage())
                .build();
    }
}
