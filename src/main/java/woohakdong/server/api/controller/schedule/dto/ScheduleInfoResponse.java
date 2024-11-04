package woohakdong.server.api.controller.schedule.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import woohakdong.server.domain.schedule.Schedule;

@Builder
public record ScheduleInfoResponse(
        Long scheduleId,
        String scheduleTitle,
        String scheduleContent,
        LocalDateTime scheduleDateTime,
        String scheduleColor
) {
    public static ScheduleInfoResponse from(Schedule schedule) {
        return ScheduleInfoResponse.builder()
                .scheduleId(schedule.getScheduleId())
                .scheduleTitle(schedule.getScheduleTitle())
                .scheduleContent(schedule.getScheduleContent())
                .scheduleDateTime(schedule.getScheduleDateTime())
                .scheduleColor(schedule.getScheduleColor())
                .build();
    }
}
