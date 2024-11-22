package woohakdong.server.domain.clubmember;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ClubMemberRole {

    PRESIDENT("회장", 1),
    VICEPRESIDENT("부회장", 2),
    SECRETARY("총무", 3),
    OFFICER("임원", 4),
    MEMBER("회원", 5)
    ;

    private final String role;
    private final int authority;

    public boolean hasAuthorityOf(ClubMemberRole role) {
        return this.authority <= role.authority;
    }
}
