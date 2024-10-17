package woohakdong.server.domain.order;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum OrderStatus {
    INIT("주문 생성"),
    COMPLETE("주문 완료");

    private final String description;
}
