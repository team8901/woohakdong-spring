package woohakdong.server.domain.member;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberGender {

    MAN("남자"),
    WOMAN("여자");

    private final String gender;
}
