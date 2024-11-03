package woohakdong.server.api.controller.schedule;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import woohakdong.server.api.controller.ListWrapperResponse;
import woohakdong.server.api.controller.schedule.dto.ScheduleCreateRequest;
import woohakdong.server.api.controller.schedule.dto.ScheduleIdResponse;
import woohakdong.server.api.controller.schedule.dto.ScheduleInfoResponse;
import woohakdong.server.api.controller.schedule.dto.ScheduleUpdateRequest;

@Tag(name = "Schedule", description = "일정 관련 API")
public interface ScheduleControllerDocs {

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "동아리 새 일정 등록", description = "동아리에 새로운 일정을 등록합니다.")
    @ApiResponse(responseCode = "200", description = "동아리 새 일정 등록 성공", useReturnTypeSchema = true)
    public ScheduleIdResponse createSchedule(Long clubId, ScheduleCreateRequest scheduleCreateRequest);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "동아리 일정 1개 불러오기", description = "동아리의 일정 1개를 불러옵니다.")
    @ApiResponse(responseCode = "200", description = "동아리 일정 1개 불러오기 성공", useReturnTypeSchema = true)
    public ScheduleInfoResponse getSchedule(Long clubId, Long scheduleId);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "동아리 일정 수정", description = "동아리의 일정을 수정합니다.")
    @ApiResponse(responseCode = "200", description = "동아리 일정 수정 성공", useReturnTypeSchema = true)
    public ScheduleIdResponse updateSchedule(Long clubId, Long scheduleId, ScheduleUpdateRequest scheduleCreateRequest);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "동아리 일정 삭제", description = "동아리의 일정을 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "동아리 일정 삭제 성공", useReturnTypeSchema = true)
    public void deleteSchedule(Long clubId, Long scheduleId);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "동아리 일정 리스트 불러오기", description = "동아리의 일정 리스트를 불러옵니다.")
    @ApiResponse(responseCode = "200", description = "동아리 일정 리스트 불러오기 성공", useReturnTypeSchema = true)
    public ListWrapperResponse<ScheduleInfoResponse> getScheduleList(Long clubId, LocalDate date);
}
