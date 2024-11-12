package woohakdong.server.api.controller.admin.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import woohakdong.server.api.controller.admin.auth.dto.AdminInfoResponse;
import woohakdong.server.api.controller.admin.auth.dto.AdminInfoUpdateRequest;
import woohakdong.server.api.controller.admin.auth.dto.AdminJoinRequest;
import woohakdong.server.api.controller.admin.auth.dto.AdminLoginRequest;
import woohakdong.server.api.controller.auth.dto.LoginResponse;

@Tag(name = "Admin", description = "관리자 회원 관리 API")
public interface AdminAuthControllerDocs {

    @Operation(summary = "관리자 로그인", description = "관리자는 아이디와 비밀번호를 입력하여 로그인 한다.")
    @ApiResponse(responseCode = "200", description = "로그인 성공 jwt token 발급", useReturnTypeSchema = true)
    public LoginResponse login(@RequestBody AdminLoginRequest loginRequest);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "관리자 추가 가입", description = "Admin 역할을 가진 관리자가 관리자를 추가로 가입시킨다.")
    @ApiResponse(responseCode = "200", description = "관리자 추가 가입 성공", useReturnTypeSchema = true)
    public void join(@RequestBody AdminJoinRequest joinRequest);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "관리자 정보 수정", description = "아이디, 이름, 비밀번호, 이메일을 수정할 수 있다.")
    @ApiResponse(responseCode = "200", description = "관리자 정보 수정 완료", useReturnTypeSchema = true)
    public void updateInfo(@RequestBody AdminInfoUpdateRequest updateRequest);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "관리자 정보 불러오기", description = "관리자 정보를 불러온다.")
    @ApiResponse(responseCode = "200", description = "관리자 정보 불러오기 성공", useReturnTypeSchema = true)
    public AdminInfoResponse getAdminInfo();
}
