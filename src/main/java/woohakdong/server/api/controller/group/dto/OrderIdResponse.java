package woohakdong.server.api.controller.group.dto;

import lombok.Builder;
import woohakdong.server.domain.order.Order;

@Builder
public record OrderIdResponse(
        Long orderId
) {
    public static OrderIdResponse from(Order order) {
        return OrderIdResponse.builder()
                .orderId(order.getOrderId())
                .build();
    }
}
