package woohakdong.server.common.util.security;

import static woohakdong.server.common.exception.CustomErrorInfo.MEMBER_NOT_FOUND;
import static woohakdong.server.common.exception.CustomErrorInfo.MEMBER_ROLE_NOT_ADMIN;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import woohakdong.server.common.exception.CustomErrorInfo;
import woohakdong.server.common.exception.CustomException;
import woohakdong.server.common.security.jwt.CustomUserDetails;
import woohakdong.server.domain.member.Member;
import woohakdong.server.domain.member.MemberRepository;

@Component
@RequiredArgsConstructor
public class SecurityUtilImpl implements SecurityUtil {

    private final MemberRepository memberRepository;

    public Member getMember() {
        String provideId = getMemberProvideIdFromSecurityContext();

        return memberRepository.findByMemberProvideId(provideId)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
    }

    public void checkMemberIsAdmin() {
        String provideId = getMemberProvideIdFromSecurityContext();
        Member member = memberRepository.findByMemberProvideId(provideId)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        if (!member.isAdmin()) {
            throw new CustomException(MEMBER_ROLE_NOT_ADMIN);
        }
    }

    private static String getMemberProvideIdFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }
}
