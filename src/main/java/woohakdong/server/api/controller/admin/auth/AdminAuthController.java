package woohakdong.server.api.controller.admin.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import woohakdong.server.api.controller.admin.auth.dto.AdminInfoResponse;
import woohakdong.server.api.controller.admin.auth.dto.AdminInfoUpdateRequest;
import woohakdong.server.api.controller.admin.auth.dto.AdminJoinRequest;
import woohakdong.server.api.controller.admin.auth.dto.AdminLoginRequest;
import woohakdong.server.api.controller.auth.dto.LoginResponse;
import woohakdong.server.api.service.admin.auth.AdminAuthService;

@RestController
@RequestMapping("/v1/admin")
@RequiredArgsConstructor
public class AdminAuthController implements AdminAuthControllerDocs{

    private final AdminAuthService adminAuthService;

    @PostMapping("/v1/auth/admin/login")
    public LoginResponse login(@RequestBody AdminLoginRequest loginRequest) {

        return adminAuthService.login(loginRequest);
    }

    @PostMapping("/v1/auth/admin/join")
    public void join(@RequestBody AdminJoinRequest joinRequest) {

        adminAuthService.createAdmin(joinRequest);
    }

    @PostMapping("/v1/auth/admin/info")
    public void updateInfo(@RequestBody AdminInfoUpdateRequest updateRequest) {

        adminAuthService.updateAdmin(updateRequest);
    }

    @GetMapping("/v1/auth/admin/info")
    public AdminInfoResponse getAdminInfo() {

        return adminAuthService.getAdminInfo();
    }
}
