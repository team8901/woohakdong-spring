package woohakdong.server.api.controller.group.dto;

import lombok.Builder;
import woohakdong.server.domain.order.Order;

@Builder
public record GroupJoinOrderResponse(
        Long orderId
) {
    public static GroupJoinOrderResponse from(Order order) {
        return GroupJoinOrderResponse.builder()
                .orderId(order.getOrderId())
                .build();
    }
}
