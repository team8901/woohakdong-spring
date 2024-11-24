package woohakdong.server.api.controller.admin.club;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import woohakdong.server.api.controller.ListWrapperResponse;
import woohakdong.server.api.controller.admin.club.dto.AdminItemHistoryResponse;
import woohakdong.server.api.controller.admin.club.dto.ClubMemberResponse;
import woohakdong.server.api.controller.admin.club.dto.ClubStartDateResponse;
import woohakdong.server.api.controller.admin.overall.dto.CountResponse;

import java.time.LocalDate;

public interface AdminClubControllerDocs {

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "동아리 별 회원 리스트", description = "동아리 별 회원 리스트를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "동아리 별 회원 리스트 반환 성공", useReturnTypeSchema = true)
    ListWrapperResponse<ClubMemberResponse> getClubMembers(@PathVariable Long clubId,
                                                           @RequestParam(required = false) LocalDate assignedTerm);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "동아리 별 물품 수", description = "동아리 별 물품 수를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "동아리 별 물품 수 반환 성공", useReturnTypeSchema = true)
    CountResponse getClubItemCount(@PathVariable Long clubId,
                                   @RequestParam(required = false) LocalDate assignedTerm);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "동아리 시작 날", description = "동아리 시작 날을 반환합니다.")
    @ApiResponse(responseCode = "200", description = "동아리 시작 날 반환 성공", useReturnTypeSchema = true)
    ClubStartDateResponse getClubOperationPeriod(@PathVariable Long clubId);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "동아리 물품 기록 리스트", description = "동아리 물품 기록 리스트를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "동아리 물품 기록 리스트 반환 성공", useReturnTypeSchema = true)
    ListWrapperResponse<AdminItemHistoryResponse> getItemHistory(@PathVariable Long clubId,
                                                                 @RequestParam(required = false) LocalDate assignedTerm);
}
