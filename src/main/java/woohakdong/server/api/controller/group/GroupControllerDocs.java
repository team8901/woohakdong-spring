package woohakdong.server.api.controller.group;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import woohakdong.server.api.controller.group.dto.GroupJoinConfirmRequest;
import woohakdong.server.api.controller.group.dto.GroupJoinOrderRequest;
import woohakdong.server.api.controller.group.dto.GroupJoinOrderResponse;
import woohakdong.server.api.controller.group.dto.PortOneWebhookRequest;

@Tag(name = "Group", description = "동아리 가입 및 모임 가입 관련 API")
public interface GroupControllerDocs {

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "동아리/그룹 가입 요청(Order 생성)", description = "동아리/그룹 가입을 위해 Order를 생성합니다.")
    @ApiResponse(responseCode = "200", description = "동아리 가입 요청 성공", useReturnTypeSchema = true)
    GroupJoinOrderResponse createClubJoinOrder(Long groupId, GroupJoinOrderRequest request);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "동아리/그룹 가입 완료 요청(결제 성공 후)", description = "결제가 완료 후, 동아리/그룹 가입을 완료를 요청합니다.")
    @ApiResponse(responseCode = "200", description = "동아리 가입 완료", useReturnTypeSchema = true)
    void completeClubJoinOrder(Long groupId, GroupJoinConfirmRequest request);

    @Operation(summary = "포트원 측 결제 완료 webhook", description = "포트원 측에서 결제가 완료되었을 때, webhook을 통해 호출되는 API")
    @ApiResponse(responseCode = "200", description = "포트원 측 결제 완료 webhook 호출 성공", useReturnTypeSchema = true)
    void portOnePaymentComplete(PortOneWebhookRequest request);
}
