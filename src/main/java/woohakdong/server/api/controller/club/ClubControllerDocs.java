package woohakdong.server.api.controller.club;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import woohakdong.server.api.controller.club.dto.ClubAccountRegisterRequest;
import woohakdong.server.api.controller.club.dto.ClubAccountValidateRequest;
import woohakdong.server.api.controller.club.dto.ClubAccountValidateResponse;
import woohakdong.server.api.controller.club.dto.ClubCreateRequest;
import woohakdong.server.api.controller.club.dto.ClubCreateResponse;
import woohakdong.server.api.controller.club.dto.ClubInfoResponse;
import woohakdong.server.api.controller.club.dto.ClubJoinGatheringInfoResponse;
import woohakdong.server.api.controller.club.dto.ClubNameValidateRequest;

@Tag(name = "Club", description = "동아리 관련 API")
public interface ClubControllerDocs {

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "동아리 이름 유효성 검증", description = "동아리 이름을 입력하면, 해당 이름이 유효한지 검증합니다.")
    @ApiResponse(responseCode = "200", description = "동아리 이름 유효성 검증 성공", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "400", description = "동아리 이름 유효성 검증 실패")
    public void validateClubName(ClubNameValidateRequest clubNameValidateRequest);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "동아리를 등록", description = "등록할 동아리 정보를 제공하면, 동아리 등록 후 동아리 id를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "동아리 등록 성공", useReturnTypeSchema = true)
    public ClubCreateResponse createClub(ClubCreateRequest clubCreateRequest);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "동아리 정보 불러오기 (진행 중)", description = "동아리 id를 입력하면, 해당 동아리의 정보를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "동아리 정보 조회 성공", useReturnTypeSchema = true)
    public ClubInfoResponse getClubInfo(Long clubId);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "동아리 정보 수정 (진행 중)", description = "수정할 동아리 정보를 제공하면, 해당 동아리의 정보를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "동아리 정보 수정 성공", useReturnTypeSchema = true)
    public ClubInfoResponse updateClubInfo(Long clubId, ClubCreateRequest clubCreateRequest);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "동아리 계좌 등록", description = "동아리에서 사용하는 계좌 정보를 등록합니다.")
    @ApiResponse(responseCode = "200", description = "동아리 계좌 등록 성공", useReturnTypeSchema = true)
    public void registerClubAccount(Long clubId, ClubAccountRegisterRequest clubAccountRegisterRequest);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "동아리 계좌 유효성 검증", description = "동아리에서 사용하는 계좌 정보를 입력하면, 해당 계좌 정보가 유효한지 검증합니다.")
    @ApiResponse(responseCode = "200", description = "동아리 계좌 유효성 검증 성공", useReturnTypeSchema = true)
    public ClubAccountValidateResponse validateClubAccount(ClubAccountValidateRequest clubAccountValidateRequest);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "동아리 가입 요청을 위한 정보 불러오기 (진행 중)", description = "동아리원들에게 제공할 링크 및 정보를 제공합니다.")
    @ApiResponse(responseCode = "200", description = "동아리 가입 요청을 위한 정보 불러오기 성공", useReturnTypeSchema = true)
    public ClubJoinGatheringInfoResponse getClubJoinInfo(Long clubId);


}
