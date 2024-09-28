package woohakdong.server.api.controller.login;

import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import woohakdong.server.common.security.jwt.JWTUtil;
import woohakdong.server.domain.Member.Member;
import woohakdong.server.domain.Member.MemberRepository;

import java.util.HashMap;
import java.util.Map;

@Controller
@ResponseBody
public class LoginController {
    private final MemberRepository memberRepository;
    private final JWTUtil jwtUtil;

    public LoginController(MemberRepository memberRepository, JWTUtil jwtUtil) {
        this.memberRepository = memberRepository;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/")
    public String mainP() {

        return "Main Controller";
    }

    @PostMapping("/v1/auth/login/social")
    public ResponseEntity<?> loginWithGoogle(@RequestBody Map<String, String> body) {
        String accessToken = body.get("accessToken");

        // Access Token을 검증 및 사용자 정보 가져오기
        String googleUserInfoEndpoint = "https://www.googleapis.com/oauth2/v3/userinfo";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(googleUserInfoEndpoint, HttpMethod.GET, entity, Map.class);
        Map<String, Object> userInfo = response.getBody();

        if (userInfo == null || userInfo.get("email") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid access token");
        }

        // 사용자 이메일로 DB 조회 및 처리
        String email = (String) userInfo.get("email");
        String providerId = (String) userInfo.get("sub"); // Google에서 제공하는 사용자 고유 ID
        String name = (String) userInfo.get("name");

        // 필요한 추가 정보들 처리
        String registrationId = "google"; // 이 경우는 Google 제공자로 설정

        String username = registrationId+"_"+providerId;

        Member member = memberRepository.findByUsername(username);
        if (member == null) {
            member = new Member();
            member.setUsername(username);
            member.setEmail(email);
            member.setName(name);
            member.setRole("USER_ROLE");
            memberRepository.save(member);
        }

        String access = jwtUtil.createJwt("access", member.getUsername(), member.getRole(), 600000L);
        String refresh = jwtUtil.createJwt("refresh", member.getUsername(), member.getRole(), 86400000L);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", access);
        tokens.put("refreshToken", refresh);

        return ResponseEntity.ok().body(tokens);
    }
}
