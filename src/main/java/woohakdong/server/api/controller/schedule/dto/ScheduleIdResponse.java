package woohakdong.server.api.controller.schedule.dto;

import lombok.Builder;

@Builder
public record ScheduleIdResponse(
        Long scheduleId
) {
}
