package woohakdong.server.domain.item;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ItemCategory {

    BOOK("book"),
    SPORT("sports"),
    DIGITAL("digital"),
    CLOTHES("clothes"),
    STATIONERY("stationery"),
    ETC("etc");

    private final String category;
}
