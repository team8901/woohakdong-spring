package woohakdong.server.api.controller.schedule.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ScheduleUpdateRequest(
        @NotBlank(message = "일정 제목은 필수입니다.")
        String scheduleTitle,

        String scheduleContent,

        LocalDateTime scheduleDateTime,

        String scheduleColor
) {
}
