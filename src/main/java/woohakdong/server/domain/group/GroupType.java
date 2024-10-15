package woohakdong.server.domain.group;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum GroupType {

    JOIN("가입"),
    EVENT("이벤트");

    private final String type;
}
