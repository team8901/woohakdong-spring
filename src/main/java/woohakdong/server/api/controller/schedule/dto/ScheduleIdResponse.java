package woohakdong.server.api.controller.schedule.dto;

import lombok.Builder;
import woohakdong.server.domain.schedule.Schedule;

@Builder
public record ScheduleIdResponse(
        Long scheduleId
) {
    public static ScheduleIdResponse from(Schedule schedule) {
        return ScheduleIdResponse.builder()
                .scheduleId(schedule.getScheduleId())
                .build();
    }
}
