package woohakdong.server.api.service.order;

import woohakdong.server.api.controller.group.dto.PaymentInfoResponse;

public interface PaymentClient {
    PaymentInfoResponse getPaymentInfo(String impUid);
}
