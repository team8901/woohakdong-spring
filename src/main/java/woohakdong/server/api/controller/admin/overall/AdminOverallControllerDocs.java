package woohakdong.server.api.controller.admin.overall;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import woohakdong.server.api.controller.ListWrapperResponse;
import woohakdong.server.api.controller.admin.overall.dto.*;

import java.time.LocalDate;

@Tag(name = "Admin overall statistics", description = "관리자 통합 통계")
public interface AdminOverallControllerDocs {

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "분기별 등록된 학교 총 개수", description = "등록된 학교 총 개수를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "학교 총 개수 반환 성공", useReturnTypeSchema = true)
    public CountResponse getTotalSchoolCount(@RequestParam(required = false)
                                                 LocalDate assignedTerm);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "분기별 등록된 동아리 총 개수", description = "분기별 등록된 동아리 총 개수를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "분기별 동아리 총 개수 반환 성공", useReturnTypeSchema = true)
    public CountResponse getTotalClubCount(@RequestParam(required = false)
                                               LocalDate assignedTerm);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "분기별 학교 리스트 반환", description = "분기별 등록된 학교 리스트를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "분기별 학교 리스트 반환 성공", useReturnTypeSchema = true)
    public ListWrapperResponse<SchoolListResponse> getAllSchools(@RequestParam(required = false)
                                                                     LocalDate assignedTerm);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "분기별 동아리 리스트 반환", description = "분기별 등록된 동아리 리스트를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "분기별 동아리 리스트 반환 성공", useReturnTypeSchema = true)
    public ListWrapperResponse<ClubListResponse> getAllClubs(@RequestParam(required = false)
                                                                 LocalDate assignedTerm);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "분기별 멤버 총 수", description = "분기별 등록된 멤버 총 수를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "분기별 멤버 총 수 반환 성공", useReturnTypeSchema = true)
    public CountResponse getTotalMemberCount(@RequestParam(required = false)
                                                 LocalDate assignedTerm);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "분기별 동아리 결제금액", description = "분기별 동아리 결제금액을 반환합니다.")
    @ApiResponse(responseCode = "200", description = "분기별 동아리 결제금액 반환 성공", useReturnTypeSchema = true)
    ClubPaymentResponse getClubPaymentByTerm(@RequestParam(required = false)
                                             LocalDate assignedTerm);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "문의 내역 반환", description = "문의 내역을 카테고리별로 반환합니다.")
    @ApiResponse(responseCode = "200", description = "문의 내역을 카테고리별 반환 성공", useReturnTypeSchema = true)
    ListWrapperResponse<InquiryListResponse> getInquiry(@RequestParam(required = false) String category);
}
