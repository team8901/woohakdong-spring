package woohakdong.server.api.controller.auth;

import org.springframework.web.bind.annotation.*;
import woohakdong.server.api.controller.auth.dto.LoginRequest;
import woohakdong.server.api.controller.auth.dto.LoginResponse;
import woohakdong.server.api.controller.auth.dto.RefreshRequest;
import woohakdong.server.api.service.auth.AuthService;

@RestController
public class AuthController implements AuthControllerDocs{

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/v1/auth/login/social")
    public LoginResponse loginWithGoogle(@RequestBody LoginRequest loginRequest) {

        return authService.login(loginRequest);
    }

    @PostMapping("/v1/auth/refresh")
    public LoginResponse refresh(@RequestBody RefreshRequest refreshRequest) {

        return authService.refresh(refreshRequest);
    }

    @PostMapping("/v1/auth/logout")
    public void logout(@RequestBody RefreshRequest refreshRequest) {

        authService.logout(refreshRequest);
    }
}
