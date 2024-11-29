package woohakdong.server.domain.item;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ItemCategory {

    BOOK("책"),
    SPORT("스포츠"),
    DIGITAL("디지털"),
    CLOTHES("의류"),
    STATIONERY("문구류"),
    ETC("기타");

    private final String category;
}
