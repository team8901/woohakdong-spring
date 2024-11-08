package woohakdong.server.api.controller.club.dto;

import lombok.Builder;

import java.time.LocalDate;
import woohakdong.server.domain.clubHistory.ClubHistory;

@Builder
public record ClubHistoryTermResponse(
        LocalDate clubHistoryUsageDate
) {
    public static ClubHistoryTermResponse from(ClubHistory clubHistory) {
        return ClubHistoryTermResponse.builder()
                .clubHistoryUsageDate(clubHistory.getClubHistoryUsageDate())
                .build();
    }
}
