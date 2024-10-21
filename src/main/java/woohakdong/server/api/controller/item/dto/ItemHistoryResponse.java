package woohakdong.server.api.controller.item.dto;

import lombok.Builder;

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
}
