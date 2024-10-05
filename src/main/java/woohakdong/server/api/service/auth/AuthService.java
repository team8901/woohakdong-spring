package woohakdong.server.api.service.auth;

import org.springframework.stereotype.Service;
import woohakdong.server.api.controller.auth.dto.LoginResponseDto;
import woohakdong.server.domain.member.MemberRepository;
import woohakdong.server.domain.refresh.RefreshRepository;
import woohakdong.server.domain.school.SchoolRepository;

@Service
public class AuthService {

    private final MemberRepository memberRepository;
    private final SchoolRepository schoolRepository;
    private final RefreshRepository refreshRepository;

    public AuthService(MemberRepository memberRepository, SchoolRepository schoolRepository, RefreshRepository refreshRepository) {
        this.memberRepository = memberRepository;
        this.schoolRepository = schoolRepository;
        this.refreshRepository = refreshRepository;
    }

    public LoginResponseDto login(String accessToken) {

    }
}
