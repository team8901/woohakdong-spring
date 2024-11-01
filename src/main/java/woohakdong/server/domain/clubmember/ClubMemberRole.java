package woohakdong.server.domain.clubmember;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ClubMemberRole {

    PRESIDENT("회장"),
    VICEPRESIDENT("부회장"),
    SECRETARY("총무"),
    OFFICER("임원"),
    MEMBER("회원")
    ;

    private final String role;
}
