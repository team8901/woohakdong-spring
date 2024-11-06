package woohakdong.server.api.service.admin.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woohakdong.server.api.controller.admin.auth.dto.AdminLoginRequest;
import woohakdong.server.api.controller.auth.dto.LoginResponse;
import woohakdong.server.common.exception.CustomErrorInfo;
import woohakdong.server.common.exception.CustomException;
import woohakdong.server.common.security.jwt.JWTUtil;
import woohakdong.server.domain.member.Member;
import woohakdong.server.domain.member.MemberRepository;
import woohakdong.server.domain.refreshToken.RefreshToken;
import woohakdong.server.domain.refreshToken.RefreshTokenRepository;

import java.util.Date;

import static woohakdong.server.common.exception.CustomErrorInfo.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminAuthService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JWTUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public LoginResponse login(AdminLoginRequest loginRequest) {
        Member admin = memberRepository.findByMemberProvideId(loginRequest.username())
                .orElseThrow(() -> new CustomException(ADMIN_MEMBER_ID_NOT_FOUND));

        if (!passwordEncoder.matches(loginRequest.password(), admin.getPassword())) {
            throw new CustomException(INVALID_ADMIN_PASSWORD);
        }

        String access = jwtUtil.createJwt("access", admin.getMemberProvideId(), admin.getMemberRole(), 600000L);
        String refresh = jwtUtil.createJwt("refresh", admin.getMemberProvideId(), admin.getMemberRole(), 86400000L);

        addRefreshEntity(admin.getMemberProvideId(), refresh, 86400000L);

        return LoginResponse.builder()
                .accessToken(access)
                .refreshToken(refresh)
                .build();
    }

    @Transactional
    public void createAdmin(String username, String email, String name) {
        if (memberRepository.findByMemberProvideId(username).isPresent()) {
            throw new CustomException(ADMIN_USERNAME_IS_ALREADY_USED);
        }

        String encryptedPassword = passwordEncoder.encode("1234");

        Member admin = Member.builder()
                .memberProvideId(username)
                .memberName(name)
                .memberRole("ADMIN_ROLE")
                .memberEmail(email)
                .password(encryptedPassword)
                .build();

        memberRepository.save(admin);
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
