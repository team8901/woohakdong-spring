package woohakdong.server.api.controller.club.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record ClubHistoryTermResponse(
        LocalDate clubHistoryUsageDate
) {
}
