package woohakdong.server.api.controller.dues;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import woohakdong.server.api.controller.ListWrapperResponse;
import woohakdong.server.api.controller.dues.dto.ClubAccountHistoryListResponse;

import java.time.LocalDate;

@Tag(name = "Dues", description = "회비 관련 API")
public interface DuesControllerDocs {

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "회비 내역 불러오기", description = "회장이 최근 업데이트한 날짜 부터 현재 시각까지 회비 내역 불러올 수 있다.")
    @ApiResponse(responseCode = "200", description = "물품 대여 기록 조회 성공", useReturnTypeSchema = true)
    public void updateTransactions(@PathVariable Long clubId);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "월별 회비 내역 조회", description = "월별 회비 내역을 조회할 수 있다.")
    @ApiResponse(responseCode = "200", description = "물품 대여 기록 조회 성공", useReturnTypeSchema = true)
    public ListWrapperResponse<ClubAccountHistoryListResponse> getMonthlyTransactions(
            @PathVariable Long clubId,
            @RequestParam(required = false) LocalDate date);
}
