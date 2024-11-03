package woohakdong.server.api.controller.schedule.dto;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ScheduleInfoResponse(
        Long scheduleId,
        String scheduleTitle,
        String scheduleContent,
        LocalDateTime scheduleDateTime,
        String scheduleColor
) {
}
