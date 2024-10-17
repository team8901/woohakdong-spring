package woohakdong.server.api.controller.group.dto;

import com.siot.IamportRestClient.response.Payment;
import lombok.Builder;

@Builder
public record PortOnePaymentResponse(
        String impUid,
        String merchantUid,
        Integer amount
){
    public static PortOnePaymentResponse from(Payment payment) {
        return PortOnePaymentResponse.builder()
                .impUid(payment.getImpUid())
                .merchantUid(payment.getMerchantUid())
                .amount(payment.getAmount().intValue())
                .build();
    }
}
