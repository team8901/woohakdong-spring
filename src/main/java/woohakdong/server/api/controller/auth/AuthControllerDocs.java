package woohakdong.server.api.controller.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import woohakdong.server.api.controller.auth.dto.LoginRequestDto;
import woohakdong.server.api.controller.auth.dto.LoginResponseDto;
import woohakdong.server.api.controller.auth.dto.RefreshRequestDto;

@Tag(name = "Auth", description = "인증 API")
public interface AuthControllerDocs {

    @Operation(summary = "google social login", description = "구글 엑세스토큰으로 사용자를 로그인 시킨다.")
    @ApiResponse(responseCode = "200", description = "로그인 성공 jwt token 발급", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "401", description = "토큰이 유효하지 않음")
    public LoginResponseDto loginWithGoogle(LoginRequestDto loginRequestDto);

    @Operation(summary = "jwt token 재발급", description = "access token이 만료되면 refresh token으로 jwt token을 재발급한다.")
    @ApiResponse(responseCode = "200", description = "재발급 성공 jwt token 발급", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "401", description = "토큰이 유효하지 않음")
    public LoginResponseDto refresh(RefreshRequestDto refreshRequestDto);
}
