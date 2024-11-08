package woohakdong.server.api.controller.clubMember;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import woohakdong.server.api.controller.ListWrapperResponse;
import woohakdong.server.api.controller.clubMember.dto.ClubMemberInfoResponse;

import java.time.LocalDate;
import java.util.List;
import woohakdong.server.domain.clubmember.ClubMemberRole;

@Tag(name = "Club Member", description = "동아리 멤버 관련 API")
public interface ClubMemberControllerDocs {

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "동아리 소속 기수별 멤버 리스트 조회하기", description = "clubId와 기수(24-1)로 동아리 소속 기수별 멤버 리스트 조회하기 쿼리 파리미터가 없다면 현재 기수 조회")
    @ApiResponse(responseCode = "200", description = "동아리 소속 기수별 멤버 리스트 조회하기 성공", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "400", description = "동아리 소속 기수별 멤버 리스트 조회하기 실패")
    public ListWrapperResponse<ClubMemberInfoResponse> getTermMembers(@PathVariable Long clubId, @PathVariable LocalDate clubMemberAssignedTerm);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "동아리 내 나의 정보 불러오기", description = "동아리 내 정보인 Role과 clubMemberId를 불러온다.")
    @ApiResponse(responseCode = "200", description = "동아리 내 나의 정보 불러오기 성공", useReturnTypeSchema = true)
    public ClubMemberInfoResponse getMyInfo(@PathVariable Long clubId);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "동아리 멤버의 역할 변경하기", description = "동아리 멤버의 Role을 변경한다.")
    @ApiResponse(responseCode = "200", description = "동아리 멤버의 역할 변경하기 성공", useReturnTypeSchema = true)
    public void changeRole(@PathVariable Long clubId, @PathVariable Long clubMemberId, @RequestParam ClubMemberRole clubMemberRole);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "동아리 멤버 상세 정보 불러오기", description = "동아리 clubMemberId로 상세 정보를 불러온다.")
    @ApiResponse(responseCode = "200", description = "동아리 멤버 상세 정보 불러오기 성공", useReturnTypeSchema = true)
    public ClubMemberInfoResponse getClubMemberInfo(@PathVariable Long clubId, @PathVariable Long clubMemberId);
}
