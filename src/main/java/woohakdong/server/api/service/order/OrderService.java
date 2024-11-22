package woohakdong.server.api.service.order;

import static woohakdong.server.common.exception.CustomErrorInfo.CLUB_ALREADY_JOINED;
import static woohakdong.server.common.exception.CustomErrorInfo.CLUB_GROUP_ALREADY_JOINED;
import static woohakdong.server.common.exception.CustomErrorInfo.ORDER_ALREADY_EXIST;
import static woohakdong.server.common.exception.CustomErrorInfo.ORDER_NOT_FOUND;
import static woohakdong.server.common.exception.CustomErrorInfo.ORDER_NOT_VALID_AMOUNT;
import static woohakdong.server.common.exception.CustomErrorInfo.ORDER_NOT_VALID_MERCHANT_UID;
import static woohakdong.server.domain.admin.adminAccount.AccountType.DEPOSIT;
import static woohakdong.server.domain.clubmember.ClubMemberRole.MEMBER;
import static woohakdong.server.domain.group.GroupType.CLUB_PAYMENT;
import static woohakdong.server.domain.group.GroupType.EVENT;
import static woohakdong.server.domain.group.GroupType.JOIN;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woohakdong.server.api.controller.group.dto.CreateOrderRequest;
import woohakdong.server.api.controller.group.dto.OrderIdResponse;
import woohakdong.server.api.controller.group.dto.PaymentCompleteReqeust;
import woohakdong.server.api.controller.group.dto.PaymentInfoResponse;
import woohakdong.server.api.controller.group.dto.PortOneWebhookRequest;
import woohakdong.server.api.service.bank.MockBankService;
import woohakdong.server.api.service.email.EmailService;
import woohakdong.server.common.exception.CustomException;
import woohakdong.server.common.util.security.SecurityUtil;
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
import woohakdong.server.domain.order.Order;
import woohakdong.server.domain.order.OrderRepository;
import woohakdong.server.domain.payment.Payment;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final EmailService emailService;
    private final MockBankService mockBankService;
    private final PaymentClient paymentClient;

    private final SecurityUtil securityUtil;

    private final ClubMemberRepository clubMemberRepository;
    private final GroupRepository groupRepository;
    private final OrderRepository orderRepository;
    private final AdminAccountRepository adminAccountRepository;
    private final AdminAccountHistoryRepository adminAccountHistoryRepository;

    @Transactional
    public OrderIdResponse registerOrder(Long groupId, CreateOrderRequest request) {
        Member member = securityUtil.getMember();
        Group group = groupRepository.getById(groupId);

        validateOrderCreation(request);
        validateGroupMembership(group, member);

        Order order = Order.create(request.merchantUid(), member, group);
        orderRepository.save(order);

        return OrderIdResponse.from(order);
    }

    @Transactional
    public void confirmOrderPayment(Long groupId, PaymentCompleteReqeust request, LocalDate date) {
        Member member = securityUtil.getMember();
        Order order = orderRepository.getById(request.orderId());

        if (order.isOrderComplete()) {
            return;
        }

        Group group = groupRepository.getById(groupId);

        if (!order.verifyGroupAndMember(group, member)) {
            throw new CustomException(ORDER_NOT_FOUND);
        }

        Club club = group.getClub();

        PaymentInfoResponse paymentInfoResponse = checkOrderWithPaymentClient(request.impUid(), order);
        savePaymentFromOrder(paymentInfoResponse, order);

        if (group.isTypeOf(JOIN)) {
            saveNewClubMember(club, member, date);
            sendClubInviteEmailToNewMember(member, club, group);
        }

        if (group.isTypeOf(CLUB_PAYMENT)) {
            club.extendClubExpirationDate();
        }

        saveAdminAccount(order, group, member);
        mockBankService.transferClubFee(member.getMemberId(), group.getGroupId());
    }

    @Transactional
    public void portOnePaymentComplete(PortOneWebhookRequest request, LocalDate date) {
        Order order = orderRepository.getByOrderMerchantUid(request.merchantUid());

        if (order.isOrderComplete()) {
            return;
        }

        Group group = order.getGroup();
        Member member = order.getMember();
        Club club = group.getClub();

        PaymentInfoResponse paymentInfoResponse = checkOrderWithPaymentClient(request.impUid(), order);
        savePaymentFromOrder(paymentInfoResponse, order);
        saveAdminAccount(order, group, member);

        if (group.isTypeOf(JOIN)) {
            saveNewClubMember(club, member, date);
            sendClubInviteEmailToNewMember(member, club, group);
        }

        if (group.isTypeOf(CLUB_PAYMENT)) {
            club.extendClubExpirationDate();
        }

        mockBankService.transferClubFee(member.getMemberId(), group.getGroupId());
    }

    protected PaymentInfoResponse checkOrderWithPaymentClient(String impUid, Order order) {
        PaymentInfoResponse paymentInfoResponse = paymentClient.getPaymentInfo(impUid);

        if (!order.verifyOrderMerchantUid(paymentInfoResponse.merchantUid())) {
            throw new CustomException(ORDER_NOT_VALID_MERCHANT_UID);
        }

        if (!order.verifyOrderAmount(paymentInfoResponse.amount())) {
            throw new CustomException(ORDER_NOT_VALID_AMOUNT);
        }
        return paymentInfoResponse;
    }

    protected void validateOrderCreation(CreateOrderRequest request) {
        if (orderRepository.existsByOrderMerchantUid(request.merchantUid())) {
            throw new CustomException(ORDER_ALREADY_EXIST);
        }
    }

    protected void validateGroupMembership(Group group, Member member) {
        Club club = group.getClub();
        if (group.isTypeOf(JOIN) && clubMemberRepository.existsByClubAndMember(club, member)) {
            throw new CustomException(CLUB_ALREADY_JOINED);
        }
        if (group.isTypeOf(EVENT)) {
            // TODO : 이벤트 그룹 가입 여부 확인
            throw new CustomException(CLUB_GROUP_ALREADY_JOINED);
        }
    }

    protected void savePaymentFromOrder(PaymentInfoResponse response, Order order) {
        Payment payment = Payment.create(response.amount(), response.impUid(), response.merchantUid());
        order.completeOrder(payment);
    }

    protected void saveNewClubMember(Club group, Member member, LocalDate now) {
        ClubMember clubMember = ClubMember.create(group, getAssignedTerm(now), MEMBER, member);
        clubMemberRepository.save(clubMember);
    }

    protected void sendClubInviteEmailToNewMember(Member member, Club club, Group group) {
        emailService.sendEmailForGroupJoin(member.getMemberName(), member.getMemberEmail(), club.getClubName(),
                club.getClubDescription(), group.getGroupChatLink(), group.getGroupChatPassword());
    }

    protected void saveAdminAccount(Order order, Group group, Member member) {
        AdminAccount adminAccount = adminAccountRepository.getFirstOne();

        Long updatedBalance = adminAccount.getAdminAccountAmount();
        updatedBalance -= order.getOrderAmount();

        AdminAccountHistory history = AdminAccountHistory.create(DEPOSIT, LocalDate.now(), updatedBalance,
                Long.valueOf(order.getOrderAmount()), adminAccount,
                group.getGroupName() + "의 회비 결제 " + member.getMemberName() + "의 회비");
        adminAccountHistoryRepository.save(history);

        adminAccount.setAdminAccountAmount(updatedBalance);
        adminAccountRepository.save(adminAccount);
    }

    private LocalDate getAssignedTerm(LocalDate now) {
        int year = now.getYear();
        int semester = now.getMonthValue() <= 6 ? 1 : 7; // 1: 1학기, 7: 2학기
        return LocalDate.of(year, semester, 1);
    }
}
