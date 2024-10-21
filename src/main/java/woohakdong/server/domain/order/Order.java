package woohakdong.server.domain.order;

import static woohakdong.server.domain.order.OrderStatus.INIT;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import woohakdong.server.domain.group.Group;
import woohakdong.server.domain.member.Member;
import woohakdong.server.domain.payment.Payment;

@Entity
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
@Table(name = "\"order\"")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @Column(nullable = false, unique = true)
    private String orderMerchantUid;

    @Column(nullable = false)
    private LocalDateTime orderAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;

    @Column(nullable = false)
    private Integer orderAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @Builder
    public Order(Long orderId, String orderMerchantUid, LocalDateTime orderAt, Integer orderAmount, Member member,
                 Group group) {
        this.orderId = orderId;
        this.orderMerchantUid = orderMerchantUid;
        this.orderAt = orderAt;
        this.orderStatus = INIT;
        this.orderAmount = orderAmount;
        this.member = member;
        this.group = group;
    }

    public boolean isAmountValid(Integer amount) {
        return this.orderAmount.equals(amount);
    }

    public boolean isOrderComplete() {
        return this.orderStatus.equals(OrderStatus.COMPLETE);
    }

    public void completeOrder(Payment payment) {
        this.payment = payment;
        this.orderStatus = OrderStatus.COMPLETE;
    }
}
