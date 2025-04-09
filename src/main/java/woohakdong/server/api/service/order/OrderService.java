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
import woohakdong.server.common.util.date.DateUtil;
import woohakdong.server.common.util.security.SecurityUtil;
import woohakdong.server.domain.admin.adminAccount.AdminAccount;
import woohakdong.server.domain.admin.adminAccount.AdminAccountRepository;
import woohakdong.server.domain.admin.adminAccountHistory.AdminAccountHistory;
import woohakdong.server.domain.admin.adminAccountHistory.AdminAccountHistoryRepository;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.clubHistory.ClubHistory;
import woohakdong.server.domain.clubHistory.ClubHistoryRepository;
import woohakdong.server.domain.clubmember.ClubMember;
import woohakdong.server.domain.clubmember.ClubMemberRepository;
import woohakdong.server.domain.group.Group;
import woohakdong.server.domain.group.GroupRepository;
import woohakdong.server.domain.groupmember.GroupMemberRepository;
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
    private final DateUtil dateUtil;

    private final ClubMemberRepository clubMemberRepository;
    private final GroupRepository groupRepository;
    private final OrderRepository orderRepository;
    private final AdminAccountRepository adminAccountRepository;
    private final AdminAccountHistoryRepository adminAccountHistoryRepository;
    private final ClubHistoryRepository clubHistoryRepository;
    private final GroupMemberRepository groupMemberRepository;

    @Transactional
    public OrderIdResponse registerOrder(Long groupId, CreateOrderRequest request, LocalDate date) {
        Member member = securityUtil.getMember();
        Group group = groupRepository.getById(groupId);

        validateOrderCreation(request);
        validateGroupMembership(group, member, date);

        Order order = Order.create(request.merchantUid(), member, group);
        orderRepository.save(order);

        return OrderIdResponse.from(order);
    }

    @Transactional
    public void confirmOrderPayment(Long groupId, PaymentCompleteReqeust request, LocalDate date) {
        Member member = securityUtil.getMember();
        Order order = orderRepository.getByIdWithLock(request.orderId());

        if (order.isOrderComplete()) {
            return;
        }

        Group group = groupRepository.getById(groupId);

        if (!order.verifyGroupAndMember(group, member)) {
            throw new CustomException(ORDER_NOT_FOUND);
        }

        Club club = group.getClub();

        PaymentInfoResponse response = checkOrderWithPaymentClient(request.impUid(), order);
        Payment payment = Payment.create(response.amount(), response.impUid(), response.merchantUid());
        order.completeOrder(payment);
        orderRepository.save(order);

        saveAdminAccount(order, group, member);

        if (group.isTypeOf(JOIN)) {
            saveNewClubMember(club, member, date);
            sendClubInviteEmailToNewMember(member, club, group);
            mockBankService.transferClubFee(member.getMemberId(), group.getGroupId());
        }

        if (group.isTypeOf(CLUB_PAYMENT)) {
            club.extendClubExpirationDate();
            LocalDate expirationDate = club.getClubExpirationDate();
            LocalDate newAssignedTerm = expirationDate.plusDays(1);
            LocalDate pastAssignedTerm = newAssignedTerm.minusMonths(6);

            ClubHistory clubHistory = ClubHistory.create(club, newAssignedTerm);
            clubHistoryRepository.save(clubHistory);

            ClubMember clubMember = clubMemberRepository.getByClubAndMemberAndAssignedTerm(club, member,
                    pastAssignedTerm);
            ClubMember newPresident = ClubMember.createFromExisting(clubMember, newAssignedTerm);
            clubMemberRepository.save(newPresident);
        }

        if (group.isTypeOf(EVENT)) {
            ClubMember clubMember = clubMemberRepository.getByClubAndMemberAndAssignedTerm(club, member,
                    dateUtil.getAssignedTerm(date));
            group.joinNewMember(clubMember);
            groupRepository.save(group);
        }

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

        PaymentInfoResponse response = checkOrderWithPaymentClient(request.impUid(), order);
        Payment payment = Payment.create(response.amount(), response.impUid(), response.merchantUid());
        order.completeOrder(payment);
        orderRepository.save(order);

        saveAdminAccount(order, group, member);

        if (group.isTypeOf(JOIN)) {
            saveNewClubMember(club, member, date);
            sendClubInviteEmailToNewMember(member, club, group);
            mockBankService.transferClubFee(member.getMemberId(), group.getGroupId());
        }

        if (group.isTypeOf(CLUB_PAYMENT)) {
            club.extendClubExpirationDate();
            LocalDate expirationDate = club.getClubExpirationDate();
            LocalDate newAssignedTerm = expirationDate.plusDays(1);
            LocalDate pastAssignedTerm = newAssignedTerm.minusMonths(6);

            ClubHistory clubHistory = ClubHistory.create(club, newAssignedTerm);
            clubHistoryRepository.save(clubHistory);

            ClubMember clubMember = clubMemberRepository.getByClubAndMemberAndAssignedTerm(club, member,
                    pastAssignedTerm);
            ClubMember newPresident = ClubMember.createFromExisting(clubMember, newAssignedTerm);
            clubMemberRepository.save(newPresident);
        }

        if (group.isTypeOf(EVENT)) {
            ClubMember clubMember = clubMemberRepository.getByClubAndMemberAndAssignedTerm(club, member,
                    dateUtil.getAssignedTerm(date));
            group.joinNewMember(clubMember);
            groupRepository.save(group);
        }
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

    protected void validateGroupMembership(Group group, Member member, LocalDate date) {
        Club club = group.getClub();
        if (group.isTypeOf(JOIN) && clubMemberRepository.existsByClubAndMember(club, member)) {
            throw new CustomException(CLUB_ALREADY_JOINED);
        }

        if (group.isTypeOf(EVENT)) {
            LocalDate assignedTerm = dateUtil.getAssignedTerm(date);
            ClubMember clubMember = clubMemberRepository.getByClubAndMemberAndAssignedTerm(club, member, assignedTerm);
            if (groupMemberRepository.checkAlreadyJoined(group, clubMember)) {
                throw new CustomException(CLUB_GROUP_ALREADY_JOINED);
            }
        }
    }

    protected void saveNewClubMember(Club group, Member member, LocalDate date) {
        ClubMember clubMember = ClubMember.create(group, dateUtil.getAssignedTerm(date), MEMBER, member);
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

        String adminHistoryContent = "";
        if (group.isTypeOf(JOIN)) {
            adminHistoryContent = member.getMemberName() + "의" + group.getGroupName() + " 회비 결제";
            if (adminHistoryContent.length() > 15) {
                adminHistoryContent = adminHistoryContent.substring(0, 15);
            }
        }

        if (group.isTypeOf(CLUB_PAYMENT)) {
            adminHistoryContent = group.getGroupName();
        }

        AdminAccountHistory history = AdminAccountHistory.create(DEPOSIT, LocalDate.now(), updatedBalance,
                Long.valueOf(order.getOrderAmount()), adminAccount, adminHistoryContent);
        adminAccountHistoryRepository.save(history);

        adminAccount.setAdminAccountAmount(updatedBalance);
        adminAccountRepository.save(adminAccount);
    }

}
