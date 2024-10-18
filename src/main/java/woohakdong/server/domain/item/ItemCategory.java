package woohakdong.server.domain.item;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ItemCategory {

    BOOK("book"),
    SPORTS("sports"),
    SUPPLIES("supplies"),
    ETC("etc");

    private final String category;
}
