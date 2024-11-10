package woohakdong.server.domain.payment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @Column(nullable = false, unique = true)
    private String paymentMerchantUid;

    @Column(nullable = false, unique = true)
    private String paymentImpUid;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Column(nullable = false)
    private Integer paymentAmount;

    @Builder
    private Payment(Integer paymentAmount, String paymentImpUid, String paymentMerchantUid, PaymentStatus paymentStatus) {
        this.paymentAmount = paymentAmount;
        this.paymentImpUid = paymentImpUid;
        this.paymentMerchantUid = paymentMerchantUid;
        this.paymentStatus = paymentStatus;
    }

    public static Payment create(Integer paymentAmount, String paymentImpUid, String paymentMerchantUid) {
        return Payment.builder()
                .paymentAmount(paymentAmount)
                .paymentImpUid(paymentImpUid)
                .paymentMerchantUid(paymentMerchantUid)
                .paymentStatus(PaymentStatus.PAYMENT)
                .build();
    }
}
