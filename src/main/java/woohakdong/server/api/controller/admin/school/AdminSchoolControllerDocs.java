package woohakdong.server.api.controller.admin.school;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import woohakdong.server.api.controller.ListWrapperResponse;
import woohakdong.server.api.controller.admin.overall.dto.ClubListResponse;
import woohakdong.server.api.controller.admin.overall.dto.CountResponse;

public interface AdminSchoolControllerDocs {

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "학교별 동아리 개수", description = "학교별 동아리 총 개수를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "학교별 동아리 총 개수 반환 성공", useReturnTypeSchema = true)
    public CountResponse getClubCountBySchool(@PathVariable Long schoolId);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "학교 별 회원 수", description = "학교 별 회원 수를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "학교 별 회원 수 반환 성공", useReturnTypeSchema = true)
    public CountResponse getMemberCountBySchool(@PathVariable Long schoolId);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "학교 별 물품 개수", description = "학교 별 물품 총 개수를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "학교 별 물품 총 개수 반환 성공", useReturnTypeSchema = true)
    public CountResponse getItemCountBySchool(@PathVariable Long schoolId);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "학교 별 동아리 리스트 반환", description = "학교 별 동아리 리스트를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "학교 별 동아리 리스트 반환 성공", useReturnTypeSchema = true)
    public ListWrapperResponse<ClubListResponse> getClubListBySchool(@PathVariable Long schoolId);
}
