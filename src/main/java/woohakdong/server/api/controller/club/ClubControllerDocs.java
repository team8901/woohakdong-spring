package woohakdong.server.api.controller.club;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import woohakdong.server.api.controller.ListWrapperResponse;
import woohakdong.server.api.controller.club.dto.ClubAccountRegisterRequest;
import woohakdong.server.api.controller.club.dto.ClubAccountResponse;
import woohakdong.server.api.controller.club.dto.ClubAccountValidateRequest;
import woohakdong.server.api.controller.club.dto.ClubAccountValidateResponse;
import woohakdong.server.api.controller.club.dto.ClubCreateRequest;
import woohakdong.server.api.controller.club.dto.ClubHistoryTermResponse;
import woohakdong.server.api.controller.club.dto.ClubIdResponse;
import woohakdong.server.api.controller.club.dto.ClubInfoResponse;
import woohakdong.server.api.controller.club.dto.ClubJoinGroupInfoResponse;
import woohakdong.server.api.controller.club.dto.ClubNameValidateRequest;
import woohakdong.server.api.controller.club.dto.ClubUpdateRequest;

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
    public ClubIdResponse createClub(ClubCreateRequest clubCreateRequest);

    @Operation(summary = "동아리 정보 불러오기", description = "동아리 id를 입력하면, 해당 동아리의 정보를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "동아리 정보 조회 성공", useReturnTypeSchema = true)
    public ClubInfoResponse getClubInfo(Long clubId);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "동아리 정보 수정", description = "수정할 동아리 정보를 제공하면, 해당 동아리의 정보를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "동아리 정보 수정 성공", useReturnTypeSchema = true)
    public ClubInfoResponse updateClubInfo(Long clubId, ClubUpdateRequest clubCreateRequest);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "동아리 계좌 정보 불러오기", description = "동아리에서 사용하는 계좌 정보를 불러옵니다.")
    @ApiResponse(responseCode = "200", description = "동아리 계좌 정보 조회 성공", useReturnTypeSchema = true)
    public ClubAccountResponse getClubAccount(@PathVariable Long clubId);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "동아리 계좌 등록", description = "동아리에서 사용하는 계좌 정보를 등록합니다.")
    @ApiResponse(responseCode = "200", description = "동아리 계좌 등록 성공", useReturnTypeSchema = true)
    public void registerClubAccount(Long clubId, ClubAccountRegisterRequest clubAccountRegisterRequest);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "동아리 계좌 유효성 검증", description = "동아리에서 사용하는 계좌 정보를 입력하면, 해당 계좌 정보가 유효한지 검증합니다.")
    @ApiResponse(responseCode = "200", description = "동아리 계좌 유효성 검증 성공", useReturnTypeSchema = true)
    public ClubAccountValidateResponse validateClubAccount(ClubAccountValidateRequest clubAccountValidateRequest);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "동아리 가입 요청을 위한 정보 불러오기", description = "동아리원들에게 제공할 링크 및 정보를 제공합니다.")
    @ApiResponse(responseCode = "200", description = "동아리 가입 요청을 위한 정보 불러오기 성공", useReturnTypeSchema = true)
    public ClubJoinGroupInfoResponse getClubJoinInfo(Long clubId);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "가입한 동아리 목록 불러오기", description = "가입한 동아리 목록을 불러옵니다.")
    @ApiResponse(responseCode = "200", description = "가입한 동아리 목록 불러오기 성공", useReturnTypeSchema = true)
    public ListWrapperResponse<ClubInfoResponse> getJoinedClubs();

    @Operation(summary = "동아리 이름으로 찾기", description = "동아리 이름을 입력하면, 해당 동아리의 정보를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "동아리 이름으로 찾기 성공", useReturnTypeSchema = true)
    public ClubInfoResponse getClubInfoByEnglishName(String clubEnglishName);

    @Operation(summary = "동아리 가입 기수 리스트 조회하기", description = "동아리가 우학동을 사용한 분기를 리스트로 반환합니다.")
    @ApiResponse(responseCode = "200", description = "동아리 가입 기수 리스트 반환 성공", useReturnTypeSchema = true)
    public ListWrapperResponse<ClubHistoryTermResponse> getClubHistory(@PathVariable Long clubId);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "동아리 사용 가능 여부 확인하기", description = "서비스 사용료 납부에 따른 동아리 사용 가능 여부를 확인합니다.")
    @ApiResponse(responseCode = "200", description = "동아리 사용 가능")
    public void checkClubExpired(Long clubId);
}
