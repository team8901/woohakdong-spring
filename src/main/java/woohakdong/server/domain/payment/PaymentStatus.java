package woohakdong.server.domain.payment;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PaymentStatus {

    PAYMENT("결제 완료");

    private final String status;
}
