package woohakdong.server.api.service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import woohakdong.server.api.controller.auth.dto.LoginRequest;
import woohakdong.server.api.controller.auth.dto.LoginResponse;
import woohakdong.server.api.controller.auth.dto.RefreshRequest;
import woohakdong.server.common.exception.CustomException;
import woohakdong.server.common.security.jwt.JWTUtil;
import woohakdong.server.domain.member.Member;
import woohakdong.server.domain.member.MemberRepository;
import woohakdong.server.domain.refresh.RefreshToken;
import woohakdong.server.domain.refresh.RefreshTokenRepository;
import woohakdong.server.domain.school.School;
import woohakdong.server.domain.school.SchoolRepository;

import java.util.Date;
import java.util.Map;

import static woohakdong.server.common.exception.CustomErrorInfo.*;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final SchoolRepository schoolRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JWTUtil jwtUtil;

    public LoginResponse login(LoginRequest loginRequest) {
        String accessToken = loginRequest.accessToken();

        // Access Token을 검증 및 사용자 정보 가져오기
        String googleUserInfoEndpoint = "https://www.googleapis.com/oauth2/v3/userinfo";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(googleUserInfoEndpoint, HttpMethod.GET, entity, Map.class);
        Map<String, Object> userInfo = response.getBody();

        if (userInfo == null || userInfo.get("email") == null) {
            throw new CustomException(INVALID_ACCESS_TOKEN);
        }

        // 사용자 이메일로 DB 조회 및 처리
        String email = (String) userInfo.get("email");
        String providerId = (String) userInfo.get("sub"); // Google에서 제공하는 사용자 고유 ID
        String name = (String) userInfo.get("name");

        //학교 이메일 체크
        School school = checkSchoolDomain(email);

        // 필요한 추가 정보들 처리
        String registrationId = "google"; // 이 경우는 Google 제공자로 설정

        String provideId = registrationId + "_" + providerId;

        Member member = memberRepository.findByMemberProvideId(provideId);
        if (member == null) {
            member = Member.builder()
                    .memberProvideId(provideId)
                    .memberName(name)
                    .memberEmail(email)
                    .memberRole("USER_ROLE")
                    .school(school)
                    .build();
            memberRepository.save(member);
        }

        String access = jwtUtil.createJwt("access", member.getMemberProvideId(), member.getMemberRole(), 600000L);
        String refresh = jwtUtil.createJwt("refresh", member.getMemberProvideId(), member.getMemberRole(), 86400000L);

        //Refresh 토큰 저장
        addRefreshEntity(provideId, refresh, 86400000L);

        LoginResponse loginResponse = new LoginResponse(access, refresh);
        return loginResponse;
    }

    public LoginResponse refresh(RefreshRequest refreshRequest) {
        String refreshToken = refreshRequest.refreshToken();

        //refresh token이 없다면
        if (refreshToken == null) {
            throw new CustomException(REFRESH_TOKEN_IS_NULL);
        }

        //expired check
        if (jwtUtil.isExpired(refreshToken)) {
            throw new CustomException(REFRESH_TOKEN_EXPIRED);
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(refreshToken);

        if (!category.equals("refresh")) {
            throw new CustomException(INVALID_REFRESH_TOKEN);
        }

        String provideId = jwtUtil.getProvideId(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        //make new JWT
        String newAccess = jwtUtil.createJwt("access", provideId, role, 600000L);
        String newRefresh = jwtUtil.createJwt("refresh", provideId, role, 86400000L);

        //Refresh 토큰 저장 DB에 기존의 Refresh 토큰 삭제 후 새 Refresh 토큰 저장
        refreshTokenRepository.deleteByRefresh(refreshToken);
        addRefreshEntity(provideId, newRefresh, 86400000L);

        LoginResponse loginResponse = new LoginResponse(newAccess, newRefresh);

        return loginResponse;
    }

    public void logout(RefreshRequest refreshRequest) {
        String refreshToken = refreshRequest.refreshToken();

        if (refreshToken == null) {
            throw new CustomException(REFRESH_TOKEN_IS_NULL);
        }

        //expired check
        if (jwtUtil.isExpired(refreshToken)) {
            throw new CustomException(REFRESH_TOKEN_EXPIRED);
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(refreshToken);

        if (!category.equals("refresh")) {
            throw new CustomException(INVALID_REFRESH_TOKEN);
        }

        //DB에 저장되어 있는지 확인
        Boolean isExist = refreshTokenRepository.existsByRefresh(refreshToken);
        if (!isExist) {
            throw new CustomException(ALREADY_EXISTS);
        }

        //로그아웃 진행
        //Refresh 토큰 DB에서 제거
        refreshTokenRepository.deleteByRefresh(refreshToken);
    }

    public School checkSchoolDomain(String email) {
        // 학교 이메일 검증
        String emailDomain = email.split("@")[1];

        School school = schoolRepository.findBySchoolDomain(emailDomain)
                .orElseThrow(() -> new CustomException(INVALID_SCHOOL_DOMAIN));

        return school;
    }

    private void addRefreshEntity(String provideId, String refresh, Long expiredMs) {

        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshToken refreshToken = RefreshToken.builder()
                .refreshProvideId(provideId)
                .refresh(refresh)
                .refreshExpiration(date.toString())
                .build();

        refreshTokenRepository.save(refreshToken);
    }
}
