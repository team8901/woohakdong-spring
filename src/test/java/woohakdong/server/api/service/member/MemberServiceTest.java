package woohakdong.server.api.service.member;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import woohakdong.server.api.controller.member.dto.CreateMemberRequest;
import woohakdong.server.common.exception.CustomException;
import woohakdong.server.common.security.jwt.CustomUserDetails;
import woohakdong.server.domain.member.Member;
import woohakdong.server.domain.member.MemberGender;
import woohakdong.server.domain.member.MemberRepository;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static woohakdong.server.common.exception.CustomErrorInfo.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberService memberService;

    @BeforeEach
    void setUp() {
        // SecurityContext에 CustomUserDetails 설정
        String provideId = "testProvideId";
        String role = "USER";
        CustomUserDetails customUserDetails = new CustomUserDetails(provideId, role);
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    @DisplayName("회원가입된 member를 업데이트 할 수 있다.")
    @Test
    void createMember() {
        // Given
        String provideId = "testProvideId";
        Member existingMember = Member.builder()
                .memberProvideId(provideId)
                .memberName("John Doe")
                .memberEmail("john.doe@example.com")
                .memberPhoneNumber("01012345678")
                .memberMajor("Computer Science")
                .memberStudentNumber("20210001")
                .memberGender(MemberGender.MAN)
                .build();

        // 실제 데이터베이스에 멤버 저장
        memberRepository.save(existingMember);

        CreateMemberRequest createMemberRequest = new CreateMemberRequest(
                "01087654321",
                "Mathematics",
                "20210002",
                MemberGender.WOMAN
        );

        // When
        memberService.createMember(createMemberRequest);

        // Then
        Member updatedMember = memberRepository.findByMemberProvideId(provideId).orElseThrow();
        assertThat(updatedMember).isNotNull();
        assertThat(updatedMember.getMemberPhoneNumber()).isEqualTo("01087654321");
        assertThat(updatedMember.getMemberMajor()).isEqualTo("Mathematics");
        assertThat(updatedMember.getMemberStudentNumber()).isEqualTo("20210002");
        assertThat(updatedMember.getMemberGender()).isEqualTo(MemberGender.WOMAN);
    }

    @DisplayName("존재하지 않는 회원은 회원 업데이트를 할 수 없다.")
    @Test
    void createMember_shouldThrowExceptionWhenMemberNotFound() {
        // Given
        String provideId = "invalidProvideId";
        CreateMemberRequest createMemberRequest = new CreateMemberRequest(
                "01087654321",
                "Mathematics",
                "20210002",
                MemberGender.WOMAN
        );

        // When & Then
        assertThatThrownBy(() -> memberService.createMember(createMemberRequest))
                .isInstanceOf(CustomException.class)
                .hasMessage(MEMBER_NOT_FOUND.getMessage());
    }
}