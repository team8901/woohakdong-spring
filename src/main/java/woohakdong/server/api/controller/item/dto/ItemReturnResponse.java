package woohakdong.server.api.controller.item.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ItemReturnResponse(
        Long itemId,
        Long itemHistoryId,
        LocalDateTime itemReturnDate
) {
}
