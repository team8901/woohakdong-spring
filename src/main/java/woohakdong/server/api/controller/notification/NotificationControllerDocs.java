package woohakdong.server.api.controller.notification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Notification", description = "알림 관련 API")
public interface NotificationControllerDocs {

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "동아리 정보 관련 알림 전송", description = "동아리 정보를 바탕으로 동아리원들에게 이메일 알림을 전송합니다.")
    @ApiResponse(responseCode = "200", description = "동아리 정보 관련 알림 전송 성공")
    public void sendNotificationWithClubInfoUpdate(Long clubId);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "동아리 일정 관련 알림 전송", description = "동아리 일정을 바탕으로 동아리원들에게 이메일 알림을 전송합니다.")
    @ApiResponse(responseCode = "200", description = "동아리 일정 관련 알림 전송 성공")
    public void sendNotificationWithSchedule(Long clubId, Long scheduleId);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "동아리 그룹 관련 알림 전송 (미구현)", description = "동아리 그룹을 바탕으로 동아리원들에게 이메일 알림을 전송합니다.")
    @ApiResponse(responseCode = "200", description = "동아리 그룹 관련 알림 전송 성공")
    public void sendNotificationWithGroup(Long clubId, Long groupId);
}
