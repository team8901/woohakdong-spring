package woohakdong.server.api.service.order;

import static org.assertj.core.api.Assertions.assertThat;
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
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import woohakdong.server.api.controller.group.dto.GroupJoinConfirmRequest;
import woohakdong.server.api.controller.group.dto.GroupJoinOrderRequest;
import woohakdong.server.api.controller.group.dto.GroupJoinOrderResponse;
import woohakdong.server.common.security.jwt.CustomUserDetails;
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

    @MockBean
    private IamportClient iamportClient;

    @BeforeEach
    void setUp() {
        // SecurityContext에 CustomUserDetails 설정
        String provideId = "testProvideId";
        String role = "USER_ROLE";
        CustomUserDetails customUserDetails = new CustomUserDetails(provideId, role);
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // memberRepository에 member 저장
        memberRepository.save(
                Member.builder()
                        .memberProvideId(provideId)
                        .memberName("John Doe")
                        .memberEmail("john.doe@example.com")
                        .memberPhoneNumber("01012345678")
                        .memberMajor("Computer Science")
                        .memberStudentNumber("20210001")
                        .memberGender(MAN)
                        .build()
        );
    }

    @DisplayName("동아리 가입을 위해 가입 요청을 진행한다.(주문 생성)")
    @Test
    void registerOrder() {
        // Given
        School school = createSchool();
        Club club = createClub(school);
        Group group = createGroup(club, 10000, JOIN);
        Member member = memberRepository.findByMemberProvideId("testProvideId").get();

        GroupJoinOrderRequest request = createClubJoinOrder("m-12315");

        // When
        GroupJoinOrderResponse response = orderService.registerOrder(group.getGroupId(), request);

        // Then
        assertThat(response).isNotNull();

        // order 생겼는지 확안하기
        Optional<Order> optionalOrder = orderRepository.findById(response.orderId());
        assertThat(optionalOrder).isPresent();
        Order order = optionalOrder.get();

        assertThat(order).extracting("orderMerchantUid", "orderAmount", "orderStatus")
                .containsExactly("m-12315", 10000, INIT);
        assertThat(order.getMember().getMemberId()).isEqualTo(member.getMemberId());
    }

    @DisplayName("동아리 가입 요청에 대해서 완료되면, clubMember에 추가한다.")
    @Test
    void confirmJoinOrder() throws IamportResponseException, IOException {
        // Given
        School school = createSchool();
        Club club = createClub(school);
        Group group = createGroup(club, 10000, JOIN);
        Member member = memberRepository.findByMemberProvideId("testProvideId").get();

        GroupJoinOrderRequest request = createClubJoinOrder("m-12315");
        GroupJoinOrderResponse response = orderService.registerOrder(group.getGroupId(), request);
        GroupJoinConfirmRequest confirmRequest = createClubJoinConfirm("imp-12315", "m-12315", response.orderId());

        // Given - Mocking
        Payment mockPayment = mock(Payment.class);
        when(mockPayment.getImpUid()).thenReturn("imp-12315");
        when(mockPayment.getMerchantUid()).thenReturn("m-12315");
        when(mockPayment.getAmount()).thenReturn(BigDecimal.valueOf(10000));

        IamportResponse<Payment> mockIamportResponse = mock(IamportResponse.class);
        when(mockIamportResponse.getResponse()).thenReturn(mockPayment);

        when(iamportClient.paymentByImpUid("imp-12315")).thenReturn(mockIamportResponse);

        // When
        orderService.confirmJoinOrder(group.getGroupId(), confirmRequest);

        // Then
        assertThat(clubMemberRepository.existsByClubAndMember(club, member)).isTrue();

        Optional<Order> optionalOrder = orderRepository.findById(confirmRequest.orderId());
        assertThat(optionalOrder).isPresent();
        Order order = optionalOrder.get();

        assertThat(order).extracting("orderStatus", "orderMerchantUid", "orderAmount")
                .containsExactly(COMPLETE, "m-12315", 10000);
    }

    private static GroupJoinConfirmRequest createClubJoinConfirm(String impUid, String merchantUid, Long orderId) {
        return GroupJoinConfirmRequest.builder()
                .impUid(impUid)
                .merchantUid(merchantUid)
                .orderId(orderId)
                .build();
    }

    private GroupJoinOrderRequest createClubJoinOrder(String merchantUid) {
        return GroupJoinOrderRequest.builder()
                .merchantUid(merchantUid)
                .build();
    }

    private Group createGroup(Club club, int amount, GroupType type) {
        Group group = Group.builder()
                .groupLink(club.getClubName() + " 동아리 가입")
                .groupName(club.getClubGeneration() + " 모집")
                .groupAmount(amount)
                .groupType(type)
                .groupLink("woohakdong.com/club/doit/gathering/13")
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

}