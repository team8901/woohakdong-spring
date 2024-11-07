package woohakdong.server.api.service.admin.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import woohakdong.server.api.controller.admin.auth.dto.AdminInfoResponse;
import woohakdong.server.api.controller.admin.auth.dto.AdminInfoUpdateRequest;
import woohakdong.server.api.controller.admin.auth.dto.AdminJoinRequest;
import woohakdong.server.common.exception.CustomException;
import woohakdong.server.common.security.jwt.CustomUserDetails;
import woohakdong.server.domain.member.Member;
import woohakdong.server.domain.member.MemberRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static woohakdong.server.common.exception.CustomErrorInfo.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class AdminAuthServiceTest {

    @Autowired
    private AdminAuthService adminAuthService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private Member existingAdmin;

    @BeforeEach
    public void setup() {
        AdminJoinRequest joinRequest = new AdminJoinRequest("testAdmin", "Admin User", "admin@example.com");
        adminAuthService.createAdmin(joinRequest);

        existingAdmin = memberRepository.findByMemberProvideId("testAdmin").orElseThrow();

        String provideId = "testAdmin";
        String role = "ADMIN_ROLE";
        CustomUserDetails customUserDetails = new CustomUserDetails(provideId, role);
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    @DisplayName("관리자를 등록할 수 있다.")
    @Test
    public void testCreateAdminSuccess() {
        // given
        AdminJoinRequest joinRequest = new AdminJoinRequest("newAdmin", "Admin Name", "admin@example.com");

        // when
        adminAuthService.createAdmin(joinRequest);

        // then
        Member savedAdmin = memberRepository.findByMemberProvideId(joinRequest.memberLoginId())
                .orElseThrow(() -> new AssertionError("Admin not found"));
        assertThat(savedAdmin)
                .extracting("memberName", "memberEmail")
                .containsExactly("Admin Name", "admin@example.com");
        assertThat(passwordEncoder.matches("1234", savedAdmin.getMemberPassword())).isTrue(); // 비밀번호 매칭 확인
    }

    @DisplayName("관리자의 아이디가 등록되어 있을 경우 에러를 발생시킨다.")
    @Test
    public void testCreateAdminUsernameAlreadyExists() {
        // given
        AdminJoinRequest duplicateRequest = new AdminJoinRequest("testAdmin", "duplicate@example.com", "Duplicate Admin");

        // when & then
        assertThatThrownBy(() -> adminAuthService.createAdmin(duplicateRequest))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ADMIN_USERNAME_IS_ALREADY_USED.getMessage());
    }

    @DisplayName("관리자의 정보를 수정할 수 있다.")
    @Test
    public void testUpdateAdminSuccess() {
        // given
        AdminInfoUpdateRequest updateRequest = new AdminInfoUpdateRequest("updatedAdmin", "Updated Name", "updated@example.com", "1234");

        // when
        adminAuthService.updateAdmin(updateRequest);

        // then
        Member updatedAdmin = memberRepository.findByMemberProvideId("updatedAdmin")
                .orElseThrow(() -> new AssertionError("Updated admin not found"));
        assertThat(updatedAdmin)
                .extracting("memberName", "memberEmail")
                .containsExactly("Updated Name", "updated@example.com");
        assertThat(passwordEncoder.matches("1234", updatedAdmin.getMemberPassword())).isTrue();
    }

    @DisplayName("관리자의 정보를 불러올 수 있다.")
    @Test
    public void testGetAdminInfo() {
        // when
        AdminInfoResponse adminInfo = adminAuthService.getAdminInfo();

        // then
        assertThat(adminInfo)
                .extracting("memberLoginId", "memberName", "memberEmail")
                .containsExactly("testAdmin", "Admin User", "admin@example.com");
    }
}