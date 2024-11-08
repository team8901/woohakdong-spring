package woohakdong.server.api.controller.group.dto;

import com.siot.IamportRestClient.response.Payment;
import lombok.Builder;

@Builder
public record PaymentInfoResponse(
        String impUid,
        String merchantUid,
        Integer amount
){
    public static PaymentInfoResponse from(Payment payment) {
        return PaymentInfoResponse.builder()
                .impUid(payment.getImpUid())
                .merchantUid(payment.getMerchantUid())
                .amount(payment.getAmount().intValue())
                .build();
    }
}
