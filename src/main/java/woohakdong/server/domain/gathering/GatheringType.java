package woohakdong.server.domain.gathering;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum GatheringType {

    JOIN("가입"),
    EVENT("이벤트");

    private final String type;
}
