package woohakdong.server.domain.member;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberRole {

    USER_ROLE("사용자"),
    ADMIN_ROLE("관리자");

    private final String role;
}
