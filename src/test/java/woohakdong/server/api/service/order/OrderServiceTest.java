package woohakdong.server.api.service.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static woohakdong.server.domain.group.GroupType.JOIN;
import static woohakdong.server.domain.member.MemberGender.MAN;
import static woohakdong.server.domain.order.OrderStatus.COMPLETE;
import static woohakdong.server.domain.order.OrderStatus.INIT;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import woohakdong.server.api.controller.group.dto.CreateOrderRequest;
import woohakdong.server.api.controller.group.dto.OrderIdResponse;
import woohakdong.server.api.controller.group.dto.PaymentCompleteReqeust;
import woohakdong.server.api.controller.group.dto.PortOneWebhookRequest;
import woohakdong.server.api.service.bank.MockBankService;
import woohakdong.server.common.security.jwt.CustomUserDetails;
import woohakdong.server.domain.admin.adminAccount.AdminAccount;
import woohakdong.server.domain.admin.adminAccount.AdminAccountRepository;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.club.ClubRepository;
import woohakdong.server.domain.clubmember.ClubMemberRepository;
import woohakdong.server.domain.group.Group;
import woohakdong.server.domain.group.GroupRepository;
import woohakdong.server.domain.group.GroupType;
import woohakdong.server.domain.member.Member;
import woohakdong.server.domain.member.MemberRepository;
import woohakdong.server.domain.order.Order;
import woohakdong.server.domain.order.OrderRepository;
import woohakdong.server.domain.school.School;
import woohakdong.server.domain.school.SchoolRepository;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private ClubMemberRepository clubMemberRepository;

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AdminAccountRepository adminAccountRepository;

    @MockBean
    private IamportClient iamportClient;

    @MockBean
    private MockBankService mockBankService;

    @BeforeEach
    void setUp() {
        String provideId = setUpSecurityContextHolder("testProvideId");
        member = createMember(provideId, "박상준", "sangjun@ajou.ac.kr");
        school = createSchool();
        club = createClub(school);
        group = createGroup(club, 10000, JOIN);
        createAdminAccount();
    }

    private Member member;
    private School school;
    private Club club;
    private Group group;

    @DisplayName("동아리 가입을 위해 가입 요청을 진행한다.(주문 생성)")
    @Test
    void registerOrder() {
        // Given
        CreateOrderRequest request = createClubJoinOrder("m-12315");

        // When
        OrderIdResponse response = orderService.registerOrder(group.getGroupId(), request);

        // Then
        Order order = orderRepository.getById(response.orderId());
        assertThat(order)
                .extracting("orderMerchantUid", "orderAmount", "orderStatus", "member.memberId")
                .containsExactly("m-12315", 10000, INIT, member.getMemberId());
    }

    @DisplayName("동아리 가입 요청을 진행하면, 주문이 완료 상태로 변경된다.")
    @Test
    void confirmOrderPayment() throws IamportResponseException, IOException {
        // Given
        Order order = createOrder(group, member, "m-12315");

        PaymentCompleteReqeust confirmRequest = createClubJoinConfirm("imp-12315", "m-12315", order.getOrderId());

        mockingIamportResponse();

        // When
        orderService.confirmOrderPayment(group.getGroupId(), confirmRequest, LocalDate.now());

        // Then
        assertThat(order)
                .extracting("orderStatus", "orderMerchantUid", "orderAmount")
                .containsExactly(COMPLETE, "m-12315", 10000);
    }

    @DisplayName("동아리 가입 요청 완료시에, 주문에 해당하는 결제가 생성된다.")
    @Test
    void confirmOrderPaymentAndAddPayment() throws IamportResponseException, IOException {
        // Given
        Order order = createOrder(group, member, "m-12315");

        PaymentCompleteReqeust confirmRequest = createClubJoinConfirm("imp-12315", "m-12315", order.getOrderId());

        // Given - Mocking
        mockingIamportResponse();

        // When
        orderService.confirmOrderPayment(group.getGroupId(), confirmRequest, LocalDate.now());

        // Then
        assertThat(order.getPayment()).isNotNull()
                .extracting("paymentAmount", "paymentImpUid", "paymentMerchantUid")
                .containsExactly(10000, "imp-12315", "m-12315");
    }

    @DisplayName("동아리 가입 요청을 완료하면, 동아리 멤버로 추가된다.")
    @Test
    void confirmOrderPaymentAndAddClubMember() throws IamportResponseException, IOException {
        // Given
        Order order = createOrder(group, member, "m-12315");

        PaymentCompleteReqeust confirmRequest = createClubJoinConfirm("imp-12315", "m-12315", order.getOrderId());

        // Given - Mocking
        mockingIamportResponse();

        // When
        orderService.confirmOrderPayment(group.getGroupId(), confirmRequest, LocalDate.now());

        // Then
        assertThat(clubMemberRepository.existsByClubAndMember(club, member)).isTrue();
    }

    @DisplayName("포트원으로부터 결제 완료 요청이 오면, 주문이 완료 상태로 변경된다.")
    @Test
    void portOnePaymentComplete() throws IamportResponseException, IOException {
        // Given
        Order order = createOrder(group, member, "m-12315");

        PortOneWebhookRequest request = createPortOneWebhookRequest("m-12315", "imp-12315");

        // Given - Mocking
        mockingIamportResponse();

        // When
        orderService.portOnePaymentComplete(request, LocalDate.now());

        // Then
        assertThat(order)
                .extracting("orderStatus", "orderMerchantUid", "orderAmount")
                .containsExactly(COMPLETE, "m-12315", 10000);
    }

    private static @NotNull String setUpSecurityContextHolder(String provideId) {
        CustomUserDetails userDetails = new CustomUserDetails(provideId, "USER_ROLE");
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        return provideId;
    }

    private Member createMember(String provideId, String name, String email) {
        Member member = Member.builder()
                .memberProvideId(provideId)
                .memberName(name)
                .memberEmail(email)
                .memberPhoneNumber("01012345678")
                .memberMajor("Computer Science")
                .memberStudentNumber("20210001")
                .memberGender(MAN)
                .build();
        return memberRepository.save(member);
    }

    private AdminAccount createAdminAccount() {
        AdminAccount adminAccount = AdminAccount.builder()
                .adminAccountBankCode("011")
                .adminAccountAmount(10000000L)
                .adminAccountBankName("농협은행")
                .adminAccountNumber("3020000011527")
                .build();
        return adminAccountRepository.save(adminAccount);
    }

    private Order createOrder(Group group, Member member, String orderMerchantUid) {
        Order order = Order.builder()
                .group(group)
                .member(member)
                .orderAmount(group.getGroupAmount())
                .orderName(group.getGroupName())
                .orderDescription(group.getGroupDescription())
                .orderStatus(INIT)
                .orderMerchantUid(orderMerchantUid)
                .orderAt(LocalDateTime.now())
                .build();
        return orderRepository.save(order);
    }

    private static PaymentCompleteReqeust createClubJoinConfirm(String impUid, String merchantUid, Long orderId) {
        return PaymentCompleteReqeust.builder()
                .impUid(impUid)
                .merchantUid(merchantUid)
                .orderId(orderId)
                .build();
    }

    private CreateOrderRequest createClubJoinOrder(String merchantUid) {
        return CreateOrderRequest.builder()
                .merchantUid(merchantUid)
                .build();
    }

    private Group createGroup(Club club, int amount, GroupType type) {
        Group group = Group.builder()
                .groupDescription(club.getClubName() + " 동아리 가입")
                .groupName(club.getClubGeneration() + " 모집")
                .groupAmount(amount)
                .groupType(type)
                .groupJoinLink("https://woohakdong.com/" + club.getClubEnglishName())
                .groupChatLink(club.getClubGroupChatLink())
                .club(club)
                .build();
        return groupRepository.save(group);
    }

    private Club createClub(School school) {
        Club club = Club.builder()
                .clubName("두잇")
                .clubEnglishName("doit")
                .clubDescription("Do it! 동아리입니다.")
                .clubGeneration("34")
                .clubGroupChatLink("https://club-group-chat-link.com")
                .clubGroupChatPassword("1234")
                .clubDues(10000)
                .clubExpirationDate(LocalDate.of(2024, 11, 19))
                .school(school)
                .build();
        return clubRepository.save(club);
    }

    private School createSchool() {
        School school = School.builder()
                .schoolName("schoolName")
                .schoolDomain("schoolDomain")
                .build();
        return schoolRepository.save(school);
    }

    private void mockingIamportResponse() throws IamportResponseException, IOException {
        Payment mockPayment = mock(Payment.class);
        when(mockPayment.getImpUid()).thenReturn("imp-12315");
        when(mockPayment.getMerchantUid()).thenReturn("m-12315");
        when(mockPayment.getAmount()).thenReturn(BigDecimal.valueOf(10000));

        IamportResponse<Payment> mockIamportResponse = mock(IamportResponse.class);
        when(mockIamportResponse.getResponse()).thenReturn(mockPayment);

        when(iamportClient.paymentByImpUid("imp-12315")).thenReturn(mockIamportResponse);

        doNothing().when(mockBankService).transferClubFee(anyLong(), anyLong());
    }

    private PortOneWebhookRequest createPortOneWebhookRequest(String merchantUid, String impUid) {
        return PortOneWebhookRequest.builder()
                .merchantUid(merchantUid)
                .impUid(impUid)
                .status("paid")
                .build();
    }
}