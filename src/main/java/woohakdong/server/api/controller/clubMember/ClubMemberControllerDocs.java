package woohakdong.server.api.controller.clubMember;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import woohakdong.server.api.controller.clubMember.dto.ClubMemberInfoResponse;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Club", description = "동아리 멤버 관련 API")
public interface ClubMemberControllerDocs {

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "동아리 소속 멤버 리스트 조회", description = "동아리 소속 멤버 리스트 조회하기")
    @ApiResponse(responseCode = "200", description = "동아리 소속 멤버 리스트 조회 성공", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "400", description = "동아리 소속 멤버 리스트 조회 실패")
    public List<ClubMemberInfoResponse> getMembers(@PathVariable Long clubId);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "동아리 소속 기수별 멤버 리스트 조회하기", description = "clubId와 기수(24-1)로 동아리 소속 기수별 멤버 리스트 조회하기")
    @ApiResponse(responseCode = "200", description = "동아리 소속 기수별 멤버 리스트 조회하기 성공", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "400", description = "동아리 소속 기수별 멤버 리스트 조회하기 실패")
    public List<ClubMemberInfoResponse> getTermMembers(@PathVariable Long clubId, @PathVariable LocalDate clubMemberAssignedTerm);
}
