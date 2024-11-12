package woohakdong.server.api.controller.admin.overall;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import woohakdong.server.api.controller.ListWrapperResponse;
import woohakdong.server.api.controller.admin.overall.dto.ClubListResponse;
import woohakdong.server.api.controller.admin.overall.dto.CountResponse;
import woohakdong.server.api.controller.admin.overall.dto.SchoolListResponse;

@Tag(name = "Admin overall statistics", description = "관리자 통합 통계")
public interface AdminOverallControllerDocs {

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "등록된 학교 총 개수", description = "등록된 학교 총 개수를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "학교 총 개수 반환 성공", useReturnTypeSchema = true)
    public CountResponse getTotalSchoolCount();

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "등록된 동아리 총 개수", description = "등록된 동아리 총 개수를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "동아리 총 개수 반환 성공", useReturnTypeSchema = true)
    public CountResponse getTotalClubCount();

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "학교 리스트 반환", description = "등록된 학교 리스트를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "학교 리스트 반환 성공", useReturnTypeSchema = true)
    public ListWrapperResponse<SchoolListResponse> getAllSchools();

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "동아리 리스트 반환", description = "등록된 동아리 리스트를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "동아리 리스트 반환 성공", useReturnTypeSchema = true)
    public ListWrapperResponse<ClubListResponse> getAllClubs();

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "멤버 총 수", description = "등록된 멤버 총 수를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "멤버 총 수 반환 성공", useReturnTypeSchema = true)
    public CountResponse getTotalMemberCount();
}
