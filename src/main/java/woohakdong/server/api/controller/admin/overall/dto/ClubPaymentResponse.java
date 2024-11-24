package woohakdong.server.api.controller.admin.overall.dto;

import lombok.Builder;

@Builder
public record ClubPaymentResponse(
        Long clubPayment
) {
    public static ClubPaymentResponse from(Long clubPayment) {
        return ClubPaymentResponse.builder()
                .clubPayment(clubPayment)
                .build();
    }
}
