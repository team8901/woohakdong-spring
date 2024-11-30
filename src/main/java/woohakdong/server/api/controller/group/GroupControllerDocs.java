package woohakdong.server.api.controller.group;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import woohakdong.server.api.controller.group.dto.CreateOrderRequest;
import woohakdong.server.api.controller.group.dto.GroupIdResponse;
import woohakdong.server.api.controller.group.dto.GroupInfoResponse;
import woohakdong.server.api.controller.group.dto.GroupUpdateRequest;
import woohakdong.server.api.controller.group.dto.OrderIdResponse;
import woohakdong.server.api.controller.group.dto.PaymentCompleteReqeust;
import woohakdong.server.api.controller.group.dto.PortOneWebhookRequest;

@Tag(name = "Group", description = "동아리 가입 및 모임 가입 관련 API")
public interface GroupControllerDocs {

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "동아리/그룹 가입 요청(Order 생성)", description = "동아리/그룹 가입을 위해 Order를 생성합니다.")
    @ApiResponse(responseCode = "200", description = "동아리 가입 요청 성공", useReturnTypeSchema = true)
    OrderIdResponse createClubJoinOrder(Long groupId, CreateOrderRequest request);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "동아리/그룹 가입 완료 요청(결제 성공 후)", description = "결제가 완료 후, 동아리/그룹 가입을 완료를 요청합니다.")
    @ApiResponse(responseCode = "200", description = "동아리 가입 완료", useReturnTypeSchema = true)
    void completeClubJoinOrder(Long groupId, PaymentCompleteReqeust request);

    @Operation(summary = "포트원 측 결제 완료 webhook", description = "포트원 측에서 결제가 완료되었을 때, webhook을 통해 호출되는 API")
    @ApiResponse(responseCode = "200", description = "포트원 측 결제 완료 webhook 호출 성공", useReturnTypeSchema = true)
    void portOnePaymentComplete(PortOneWebhookRequest request);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "그룹 상세 조회", description = "그룹 id를 입력하면, 해당 그룹의 상세 정보를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "그룹 상세 조회 성공", useReturnTypeSchema = true)
    GroupInfoResponse getGroupInfo(Long groupId);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "그룹 정보 수정", description = "수정할 그룹 정보를 제공하면, 해당 그룹의 정보를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "그룹 정보 수정 성공", useReturnTypeSchema = true)
    GroupIdResponse updateGroupInfo(Long groupId, GroupUpdateRequest groupUpdateRequest);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "그룹 삭제", description = "삭제할 그룹 id를 입력하면, 해당 그룹을 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "그룹 삭제 성공", useReturnTypeSchema = true)
    void deleteGroup(Long groupId);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "그룹 사용 여부 on/off", description = "활성화할 그룹 id를 입력하면, 해당 그룹의 사용 여부를 변경합니다.")
    @ApiResponse(responseCode = "200", description = "그룹 사용 가능 여부 변경 성공", useReturnTypeSchema = true)
    void changeAvailabilityOfGroup(Long groupId);
}
