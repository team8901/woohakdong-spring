package woohakdong.server.api.controller.auth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import woohakdong.server.api.controller.auth.dto.LoginRequestDto;
import woohakdong.server.api.controller.auth.dto.LoginResponseDto;
import woohakdong.server.api.controller.auth.dto.RefreshRequestDto;
import woohakdong.server.api.service.auth.AuthService;

@Controller
@ResponseBody
public class AuthController implements AuthControllerDocs{

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/")
    public String mainP() {

        return "Main Controller";
    }

    @PostMapping("/v1/auth/login/social")
    public LoginResponseDto loginWithGoogle(LoginRequestDto loginRequestDto) {

        return authService.login(loginRequestDto);
    }

    @PostMapping("/v1/auth/refresh")
    public LoginResponseDto refresh(RefreshRequestDto refreshRequestDto) {

        return authService.refresh(refreshRequestDto);
    }

    @PostMapping("/v1/auth/logout")
    public void logout(RefreshRequestDto refreshRequestDto) {

        authService.logout(refreshRequestDto);
    }
}
