package woohakdong.server.api.controller.admin.club.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ClubStartDateResponse(
        LocalDateTime startDate
) {
    public static ClubStartDateResponse from(LocalDateTime startDate) {
        return ClubStartDateResponse.builder()
                .startDate(startDate)
                .build();
    }
}
