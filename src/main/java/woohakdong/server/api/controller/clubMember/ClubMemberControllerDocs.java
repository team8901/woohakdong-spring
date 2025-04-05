package woohakdong.server.api.controller.clubMember;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import woohakdong.server.api.controller.ListWrapperResponse;
import woohakdong.server.api.controller.SliceResponse;
import woohakdong.server.api.controller.clubMember.dto.ClubMemberInfoResponse;

import java.time.LocalDate;
import woohakdong.server.domain.clubmember.ClubMemberRole;

@Tag(name = "Club Member", description = "동아리 멤버 관련 API")
public interface ClubMemberControllerDocs {

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "동아리 소속 기수별 멤버 리스트 조회하기", description = "clubId와 기수(24-1)로 동아리 소속 기수별 멤버 리스트 조회하기 쿼리 파리미터가 없다면 현재 기수 조회")
    @ApiResponse(responseCode = "200", description = "동아리 소속 기수별 멤버 리스트 조회하기 성공", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "400", description = "동아리 소속 기수별 멤버 리스트 조회하기 실패")
    public SliceResponse<ClubMemberInfoResponse> getFilteredMembers(Long clubId, LocalDate clubMemberAssignedTerm, String name, Pageable pageable);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "동아리 내 나의 정보 불러오기", description = "동아리 내 정보인 Role과 clubMemberId를 불러온다.")
    @ApiResponse(responseCode = "200", description = "동아리 내 나의 정보 불러오기 성공", useReturnTypeSchema = true)
    public ClubMemberInfoResponse getMyInfo(Long clubId);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "동아리 멤버의 역할 변경하기", description = "동아리 멤버의 Role을 변경한다.")
    @ApiResponse(responseCode = "200", description = "동아리 멤버의 역할 변경하기 성공", useReturnTypeSchema = true)
    public void changeRole(Long clubId, Long clubMemberId, ClubMemberRole clubMemberRole);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "동아리 멤버 상세 정보 불러오기", description = "동아리 clubMemberId로 상세 정보를 불러온다.")
    @ApiResponse(responseCode = "200", description = "동아리 멤버 상세 정보 불러오기 성공", useReturnTypeSchema = true)
    public ClubMemberInfoResponse getClubMemberInfo(Long clubId, Long clubMemberId);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "동아리 멤버에게 회장 권한 위임하기", description = "동아리 멤버에게 회장 권한을 위임한다.")
    @ApiResponse(responseCode = "200", description = "동아리 멤버에게 회장 권한 위임하기 성공", useReturnTypeSchema = true)
    public void passOnThePresidency(Long clubId, Long clubMemberId);
}
