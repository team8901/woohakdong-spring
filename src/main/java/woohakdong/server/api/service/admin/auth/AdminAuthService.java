package woohakdong.server.api.service.admin.auth;

import static woohakdong.server.common.exception.CustomErrorInfo.ADMIN_USERNAME_IS_ALREADY_USED;
import static woohakdong.server.common.exception.CustomErrorInfo.INVALID_ADMIN_PASSWORD;

import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woohakdong.server.api.controller.admin.auth.dto.AdminInfoResponse;
import woohakdong.server.api.controller.admin.auth.dto.AdminInfoUpdateRequest;
import woohakdong.server.api.controller.admin.auth.dto.AdminJoinRequest;
import woohakdong.server.api.controller.admin.auth.dto.AdminLoginRequest;
import woohakdong.server.api.controller.auth.dto.LoginResponse;
import woohakdong.server.common.exception.CustomException;
import woohakdong.server.common.security.jwt.JWTUtil;
import woohakdong.server.common.util.security.SecurityUtil;
import woohakdong.server.domain.member.Member;
import woohakdong.server.domain.member.MemberRepository;
import woohakdong.server.domain.refreshToken.RefreshToken;
import woohakdong.server.domain.refreshToken.RefreshTokenRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminAuthService {

    private final JWTUtil jwtUtil;
    private final SecurityUtil securityUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public LoginResponse login(AdminLoginRequest loginRequest) {
        Member admin = memberRepository.findByAdminMemberProvideId(loginRequest.memberLoginId());

        if (!passwordEncoder.matches(loginRequest.memberPassword(), admin.getMemberPassword())) {
            throw new CustomException(INVALID_ADMIN_PASSWORD);
        }

        String access = jwtUtil.createJwt("access", admin.getMemberProvideId(), admin.getMemberRole(), 600000L);
        String refresh = jwtUtil.createJwt("refresh", admin.getMemberProvideId(), admin.getMemberRole(), 86400000L);

        addRefreshEntity(admin.getMemberProvideId(), refresh, 86400000L);

        return LoginResponse.from(access, refresh);
    }

    @Transactional
    public void createAdmin(AdminJoinRequest joinRequest) {
        if (memberRepository.findByDuplicateMemberProvideId(joinRequest.memberLoginId())) {
            throw new CustomException(ADMIN_USERNAME_IS_ALREADY_USED);
        }

        String encryptedPassword = passwordEncoder.encode("1234");

        Member admin = Member.createAdmin(joinRequest.memberLoginId(), joinRequest.memberName(),
                joinRequest.memberEmail(), encryptedPassword);

        memberRepository.save(admin);
    }

    @Transactional
    public void updateAdmin(AdminInfoUpdateRequest updateRequest) {
        Member admin = securityUtil.getMember();

        if (memberRepository.findByMemberProvideId(updateRequest.memberLoginId()).isPresent()) {
            throw new CustomException(ADMIN_USERNAME_IS_ALREADY_USED);
        }

        String encryptedPassword = passwordEncoder.encode(updateRequest.memberPassword());
        admin.adminUpdate(updateRequest.memberLoginId(), updateRequest.memberName(), updateRequest.memberEmail(),
                encryptedPassword);

        memberRepository.save(admin);
    }

    public AdminInfoResponse getAdminInfo() {
        Member admin = securityUtil.getMember();
        return AdminInfoResponse.from(admin);
    }

    private void addRefreshEntity(String provideId, String refresh, Long expiredMs) {
        Date date = new Date(System.currentTimeMillis() + expiredMs);
        RefreshToken refreshToken = RefreshToken.create(provideId, refresh, date.toString());
        refreshTokenRepository.save(refreshToken);
    }
}
