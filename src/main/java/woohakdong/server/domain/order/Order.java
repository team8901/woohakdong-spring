package woohakdong.server.domain.order;

import static woohakdong.server.domain.order.OrderStatus.COMPLETE;
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
import woohakdong.server.domain.BaseEntity;
import woohakdong.server.domain.group.Group;
import woohakdong.server.domain.member.Member;
import woohakdong.server.domain.payment.Payment;

@Entity
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
@Table(name = "\"order\"")
public class Order extends BaseEntity {

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

    @Column(nullable = false)
    private String orderName;

    @Column(nullable = false)
    private String orderDescription;

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
    private Order(Long orderId, String orderMerchantUid, LocalDateTime orderAt, OrderStatus orderStatus,
                  Integer orderAmount, String orderName, String orderDescription, Member member, Group group) {
        this.orderId = orderId;
        this.orderMerchantUid = orderMerchantUid;
        this.orderAt = orderAt;
        this.orderStatus = orderStatus;
        this.orderAmount = orderAmount;
        this.member = member;
        this.group = group;
        this.orderName = orderName;
        this.orderDescription = orderDescription;
    }

    public static Order create(String orderMerchantUid, Member member, Group group) {
        return Order.builder()
                .orderMerchantUid(orderMerchantUid)
                .orderAt(LocalDateTime.now())
                .orderStatus(INIT)
                .orderAmount(group.getGroupAmount())
                .orderName(group.getGroupName())
                .orderDescription(group.getGroupDescription())
                .member(member)
                .group(group)
                .build();
    }

    public boolean isOrderComplete() {
        return this.orderStatus.equals(COMPLETE);
    }

    public void completeOrder(Payment payment) {
        this.payment = payment;
        this.orderStatus = COMPLETE;
    }

    public boolean verifyGroupAndMember(Group group, Member member) {
        return this.group.equals(group) && this.member.equals(member);
    }

    public boolean verifyOrderAmount(Integer amount) {
        return this.orderAmount.equals(amount);
    }

    public boolean verifyOrderMerchantUid(String merchantUid) {
        return this.orderMerchantUid.equals(merchantUid);
    }
}
