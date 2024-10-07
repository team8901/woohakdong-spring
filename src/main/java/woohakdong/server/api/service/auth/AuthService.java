package woohakdong.server.api.service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import woohakdong.server.api.controller.auth.dto.LoginResponseDto;
import woohakdong.server.common.exception.CustomException;
import woohakdong.server.common.security.jwt.JWTUtil;
import woohakdong.server.domain.member.Member;
import woohakdong.server.domain.member.MemberRepository;
import woohakdong.server.domain.refresh.RefreshEntity;
import woohakdong.server.domain.refresh.RefreshRepository;
import woohakdong.server.domain.school.School;
import woohakdong.server.domain.school.SchoolRepository;

import java.util.Date;
import java.util.Map;

import static woohakdong.server.common.exception.CustomErrorInfo.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final SchoolRepository schoolRepository;
    private final RefreshRepository refreshRepository;
    private final JWTUtil jwtUtil;

    public LoginResponseDto login(String accessToken) {
        // Access Token을 검증 및 사용자 정보 가져오기
        String googleUserInfoEndpoint = "https://www.googleapis.com/oauth2/v3/userinfo";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(googleUserInfoEndpoint, HttpMethod.GET, entity, Map.class);
        Map<String, Object> userInfo = response.getBody();

        if (userInfo == null || userInfo.get("email") == null) {
            throw new CustomException(INVALID_ACCESSTOKEN);
        }

        // 사용자 이메일로 DB 조회 및 처리
        String email = (String) userInfo.get("email");
        String providerId = (String) userInfo.get("sub"); // Google에서 제공하는 사용자 고유 ID
        String name = (String) userInfo.get("name");

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

        LoginResponseDto loginResponseDto = new LoginResponseDto(access, refresh);
        return loginResponseDto;
    }

    public School checkSchoolDomain(String email) {
        // 학교 이메일 검증
        String emailDomain = email.split("@")[1];

        School school = schoolRepository.findBySchoolDomain(emailDomain);
        if (school == null) {
            throw new CustomException(INVALID_SCHOOL_DOMAIN);
        }
        return school;
    }

    private void addRefreshEntity(String provideId, String refresh, Long expiredMs) {

        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshEntity refreshEntity = RefreshEntity.builder()
                .refreshProvideId(provideId)
                .refresh(refresh)
                .refreshExpiration(date.toString())
                .build();

        refreshRepository.save(refreshEntity);
    }
}
