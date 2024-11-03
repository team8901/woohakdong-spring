package woohakdong.server.api.controller.schedule;

import jakarta.validation.Valid;
import java.time.LocalDate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import woohakdong.server.api.controller.ListWrapperResponse;
import woohakdong.server.api.controller.schedule.dto.ScheduleCreateRequest;
import woohakdong.server.api.controller.schedule.dto.ScheduleIdResponse;
import woohakdong.server.api.controller.schedule.dto.ScheduleInfoResponse;
import woohakdong.server.api.controller.schedule.dto.ScheduleUpdateRequest;

@RestController
@RequestMapping("/v1/clubs/{clubId}")
public class ScheduleController implements ScheduleControllerDocs {

    @PostMapping("/schedules")
    public ScheduleIdResponse createSchedule(@PathVariable Long clubId,
                                             @Valid @RequestBody ScheduleCreateRequest scheduleCreateRequest) {
        return null;
    }

    @GetMapping("/schedules/{scheduleId}")
    public ScheduleInfoResponse getSchedule(@PathVariable Long clubId,
                                            @PathVariable Long scheduleId) {
        return null;
    }

    @PutMapping("/schedules/{scheduleId}")
    public ScheduleIdResponse updateSchedule(@PathVariable Long clubId,
                                             @PathVariable Long scheduleId,
                                             @Valid @RequestBody ScheduleUpdateRequest scheduleCreateRequest) {
        return null;
    }

    @DeleteMapping("/schedules/{scheduleId}")
    public void deleteSchedule(@PathVariable Long clubId,
                               @PathVariable Long scheduleId) {

    }

    @GetMapping("/schedules")
    public ListWrapperResponse<ScheduleInfoResponse> getScheduleList(@PathVariable Long clubId,
                                                                     @RequestParam LocalDate date) {
        return null;
    }
}