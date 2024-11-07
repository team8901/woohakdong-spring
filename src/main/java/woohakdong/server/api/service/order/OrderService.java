package woohakdong.server.api.service.order;

import static woohakdong.server.common.exception.CustomErrorInfo.CLUB_ALREADY_JOINED;
import static woohakdong.server.common.exception.CustomErrorInfo.GROUP_TYPE_NOT_FOUND;
import static woohakdong.server.common.exception.CustomErrorInfo.MEMBER_NOT_FOUND;
import static woohakdong.server.common.exception.CustomErrorInfo.ORDER_ALREADY_EXIST;
import static woohakdong.server.common.exception.CustomErrorInfo.ORDER_NOT_FOUND;
import static woohakdong.server.domain.clubmember.ClubMemberRole.MEMBER;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woohakdong.server.api.controller.group.dto.GroupJoinConfirmRequest;
import woohakdong.server.api.controller.group.dto.GroupJoinOrderRequest;
import woohakdong.server.api.controller.group.dto.GroupJoinOrderResponse;
import woohakdong.server.api.controller.group.dto.PortOnePaymentResponse;
import woohakdong.server.api.controller.group.dto.PortOneWebhookRequest;
import woohakdong.server.api.service.bank.MockBankService;
import woohakdong.server.api.service.email.EmailService;
import woohakdong.server.common.exception.CustomException;
import woohakdong.server.common.security.jwt.CustomUserDetails;
import woohakdong.server.domain.admin.adminAccount.AccountType;
import woohakdong.server.domain.admin.adminAccount.AdminAccount;
import woohakdong.server.domain.admin.adminAccount.AdminAccountRepository;
import woohakdong.server.domain.admin.adminAccountHistory.AdminAccountHistory;
import woohakdong.server.domain.admin.adminAccountHistory.AdminAccountHistoryRepository;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.clubmember.ClubMember;
import woohakdong.server.domain.clubmember.ClubMemberRepository;
import woohakdong.server.domain.group.Group;
import woohakdong.server.domain.group.GroupRepository;
import woohakdong.server.domain.member.Member;
import woohakdong.server.domain.member.MemberRepository;
import woohakdong.server.domain.order.Order;
import woohakdong.server.domain.order.OrderRepository;
import woohakdong.server.domain.payment.Payment;
import woohakdong.server.domain.payment.PaymentStatus;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final MemberRepository memberRepository;
    private final ClubMemberRepository clubMemberRepository;
    private final GroupRepository groupRepository;
    private final OrderRepository orderRepository;
    private final IamportClient iamportClient;
    private final EmailService emailService;
    private final AdminAccountRepository adminAccountRepository;
    private final AdminAccountHistoryRepository adminAccountHistoryRepository;
    private final MockBankService mockBankService;

    @Transactional
    public GroupJoinOrderResponse registerOrder(Long groupId, GroupJoinOrderRequest request) {
        Member member = getMemberFromJwtInformation();
        Group group = groupRepository.getById(groupId);

        Club club = group.getClub();

        if (orderRepository.existsByOrderMerchantUid(request.merchantUid())) {
            throw new CustomException(ORDER_ALREADY_EXIST);
        }

        switch (group.getGroupType()) {
            case JOIN:
                if (clubMemberRepository.existsByClubAndMember(club, member)) {
                    throw new CustomException(CLUB_ALREADY_JOINED);
                }
                break;
            case EVENT:
                if (!clubMemberRepository.existsByClubAndMember(club, member)) {
                    throw new CustomException(CLUB_ALREADY_JOINED);
                }

            default:
                throw new CustomException(GROUP_TYPE_NOT_FOUND);
        }

        Order order = createOrder(request, group, member);
        orderRepository.save(order);

        return GroupJoinOrderResponse.builder()
                .orderId(order.getOrderId())
                .build();
    }

    @Transactional
    public void confirmJoinOrder(Long groupId, GroupJoinConfirmRequest request) {
        Member member = getMemberFromJwtInformation();
        Order order = orderRepository.getById(request.orderId());

        if (order.isOrderComplete()) {
            return;
        }

        Group group = groupRepository.getById(groupId);

        if (!order.getGroup().equals(group) | !order.getMember().equals(member)) {
            throw new CustomException(ORDER_NOT_FOUND);
        }

        PortOnePaymentResponse paymentResponse = getPaymentInfoFromPortOne(request.impUid());
        if (!Objects.equals(paymentResponse.amount(), order.getOrderAmount()) |
                !Objects.equals(paymentResponse.merchantUid(), order.getOrderMerchantUid())) {
            throw new CustomException(ORDER_NOT_FOUND);
        }

        Payment payment = createPayment(paymentResponse);

        order.completeOrder(payment);
        orderRepository.save(order);

        ClubMember clubMember = createClubMemberWithOrder(order);
        clubMemberRepository.save(clubMember);

        saveAdminAccount(order, group, member);

        Club club = group.getClub();

        emailService.sendEmailForGroupJoin(member.getMemberName(), member.getMemberEmail(), club.getClubName(),
                club.getClubDescription(), group.getGroupChatLink(), group.getGroupChatPassword());

        mockBankService.transferClubFee(member.getMemberId(), groupId);
    }

    @Transactional
    public void portOnePaymentComplete(PortOneWebhookRequest request) {
        Order order = orderRepository.getByOrderMerchantUid(request.merchantUid());

        if (order.isOrderComplete()) {
            return;
        }

        PortOnePaymentResponse paymentResponse = getPaymentInfoFromPortOne(request.impUid());
        if (!Objects.equals(paymentResponse.amount(), order.getOrderAmount()) |
                !Objects.equals(paymentResponse.merchantUid(), order.getOrderMerchantUid())) {
            throw new CustomException(ORDER_NOT_FOUND);
        }

        Payment payment = createPayment(paymentResponse);

        order.completeOrder(payment);
        orderRepository.save(order);

        ClubMember clubMember = createClubMemberWithOrder(order);
        clubMemberRepository.save(clubMember);

        Group group = order.getGroup();
        Member member = order.getMember();
        Club club = group.getClub();

        saveAdminAccount(order, group, member);

        emailService.sendEmailForGroupJoin(member.getMemberName(), member.getMemberEmail(), club.getClubName(),
                club.getClubDescription(), group.getGroupChatLink(), group.getGroupChatPassword());

        mockBankService.transferClubFee(member.getMemberId(), group.getGroupId());
    }

    private PortOnePaymentResponse getPaymentInfoFromPortOne(String impUid) {
        try {
            return PortOnePaymentResponse.from(iamportClient.paymentByImpUid(impUid).getResponse());
        } catch (IamportResponseException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Order createOrder(GroupJoinOrderRequest request, Group group, Member member) {
        return Order.builder()
                .group(group)
                .member(member)
                .orderAt(LocalDateTime.now())
                .orderAmount(group.getGroupAmount())
                .orderMerchantUid(request.merchantUid())
                .build();
    }

    private Member getMemberFromJwtInformation() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String provideId = userDetails.getUsername();

        return memberRepository.findByMemberProvideId(provideId)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
    }

    private LocalDate getAssignedTerm() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int semester = now.getMonthValue() <= 6 ? 1 : 7; // 1: 1학기, 7: 2학기
        return LocalDate.of(year, semester, 1);
    }

    private ClubMember createClubMemberWithOrder(Order order) {
        return ClubMember.builder()
                .club(order.getGroup().getClub())
                .member(order.getMember())
                .clubJoinedDate(LocalDateTime.now().toLocalDate())
                .clubMemberRole(MEMBER)
                .clubMemberAssignedTerm(getAssignedTerm())
                .build();
    }

    private static Payment createPayment(PortOnePaymentResponse paymentResponse) {
        return Payment.builder()
                .paymentAmount(paymentResponse.amount())
                .paymentMerchantUid(paymentResponse.merchantUid())
                .paymentImpUid(paymentResponse.impUid())
                .paymentStatus(PaymentStatus.PAYMENT)
                .build();
    }

    private void saveAdminAccount(Order order, Group group, Member member) {
        Long adminAccountId = 1L;

        AdminAccount adminAccount = adminAccountRepository.getById(adminAccountId);

        Long updatedBalance = adminAccount.getAdminAccountAmount();
        updatedBalance -= order.getOrderAmount();

        AdminAccountHistory history = AdminAccountHistory.builder()
                .adminAccountHistoryInOutType(AccountType.DEPOSIT)
                .adminAccountHistoryTranDate(LocalDate.now())
                .adminAccountHistoryBalanceAmount(updatedBalance)
                .adminAccountHistoryTranAmount(Long.valueOf(order.getOrderAmount()))
                .adminAccountHistoryContent(
                        group.getGroupName() + "의 회비 결제 " + member.getMemberName() + "의 회비")           // 이체 내역 설명
                .adminAccount(adminAccount)
                .build();

        adminAccountHistoryRepository.save(history);

        adminAccount.setAdminAccountAmount(updatedBalance);
        adminAccountRepository.save(adminAccount);
    }
}
