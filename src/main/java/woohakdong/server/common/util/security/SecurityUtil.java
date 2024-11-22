package woohakdong.server.common.util.security;

import woohakdong.server.domain.member.Member;

public interface SecurityUtil {

    Member getMember();
    void checkMemberIsAdmin();
}
