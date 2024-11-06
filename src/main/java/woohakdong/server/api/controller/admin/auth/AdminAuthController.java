package woohakdong.server.api.controller.admin.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import woohakdong.server.api.controller.admin.auth.dto.AdminLoginRequest;
import woohakdong.server.api.controller.auth.dto.LoginRequest;
import woohakdong.server.api.controller.auth.dto.LoginResponse;
import woohakdong.server.api.service.admin.auth.AdminAuthService;

@RestController
@RequestMapping("/v1/admin")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    @PostMapping("/v1/auth/admin/login")
    public LoginResponse login(@RequestBody AdminLoginRequest loginRequest) {

        return adminAuthService.login(loginRequest);
    }
}
