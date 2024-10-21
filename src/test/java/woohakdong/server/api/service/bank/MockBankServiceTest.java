package woohakdong.server.api.service.bank;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static woohakdong.server.common.exception.CustomErrorInfo.BANK_INVALID_ACCOUNT_NUMBER;
import static woohakdong.server.common.exception.CustomErrorInfo.BANK_NOT_SUPPORTED;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import woohakdong.server.api.controller.club.dto.ClubAccountValidateRequest;
import woohakdong.server.api.controller.club.dto.ClubAccountValidateResponse;
import woohakdong.server.common.exception.CustomException;
import woohakdong.server.domain.admin.adminAccount.AdminAccount;
import woohakdong.server.domain.admin.adminAccount.AdminAccountRepository;
import woohakdong.server.domain.admin.adminAccountHistory.AdminAccountHistory;
import woohakdong.server.domain.admin.adminAccountHistory.AdminAccountHistoryRepository;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.club.ClubRepository;
import woohakdong.server.domain.clubAccount.ClubAccount;
import woohakdong.server.domain.clubAccount.ClubAccountRepository;
import woohakdong.server.domain.member.Member;
import woohakdong.server.domain.member.MemberRepository;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class MockBankServiceTest {

    @Autowired
    private MockBankService mockBankService;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private ClubAccountRepository clubAccountRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AdminAccountRepository adminAccountRepository;

    @Autowired
    private AdminAccountHistoryRepository adminAccountHistoryRepository;

    @DisplayName("은행 이름과 계좌번호가 주어졌을 때, 해당 계좌번호의 PIN을 반환한다.")
    @Test
    void certifyAccount() {
        // Given
        ClubAccountValidateRequest accountValidateRequest = createAccountValidateRequest("국민은행", "1234567890");

        // When
        ClubAccountValidateResponse result = mockBankService.certifyAccount(accountValidateRequest);

        // Then
        assertThat(result).isNotNull()
                .extracting("clubAccountBankName", "clubAccountNumber", "clubAccountPinTechNumber")
                .containsExactly("국민은행", "1234567890", "PIN-1234567890");
    }

    @DisplayName("존재하지 않은 은행 이름으로 인증을 시도하면 BANK_NOT_SUPPORTED이 발생한다.")
    @Test
    void certifyAccountWithWrongBankName() {
        // Given
        ClubAccountValidateRequest accountValidateRequest = createAccountValidateRequest("우학동은행", "1234567890");

        // When & Then
        assertThatThrownBy(() -> mockBankService.certifyAccount(accountValidateRequest))
                .isInstanceOf(CustomException.class)
                .hasMessage(BANK_NOT_SUPPORTED.getMessage());
    }

    @DisplayName("존재하지 않는 계좌번호로 인증을 시도하면 BANK_INVALID_ACCOUNT_NUMBER가 발생한다.")
    @Test
    void test() {
        // Given
        ClubAccountValidateRequest accountValidateRequest = createAccountValidateRequest("국민은행", "0000000000");

        // When & Then
        assertThatThrownBy(() -> mockBankService.certifyAccount(accountValidateRequest))
                .isInstanceOf(CustomException.class)
                .hasMessage(BANK_INVALID_ACCOUNT_NUMBER.getMessage());
    }

    private ClubAccountValidateRequest createAccountValidateRequest(String bankName, String accountNumber) {
        return ClubAccountValidateRequest.builder()
                .clubAccountBankName(bankName)
                .clubAccountNumber(accountNumber)
                .build();
    }

    @DisplayName("타행 계좌 이체가 성공적으로 이루어진다.")
    @Test
    @Disabled
    void transferTest() {
        // Given
        Club club = clubRepository.save(Club.builder()
                .clubName("테스트 동아리")
                .clubEnglishName("ATC")
                .clubDues(10000)
                .build());

        ClubAccount clubAccount = clubAccountRepository.save(ClubAccount.builder()
                .club(club)
                .clubAccountNumber("1000003141002")
                .clubAccountBankCode("002")
                .clubAccountPinTechNumber("000")
                .clubAccountBankName("산업은행")
                .build());

        Member member = memberRepository.save(Member.builder()
                .memberName("테스트 회원")
                .memberEmail("test@ajou.ac.kr")
                .memberProvideId("1")
                .build());

        AdminAccount adminAccount = adminAccountRepository.findById(1L).get();

        // When: 이체 서비스 호출
        mockBankService.transferClubFee(member.getMemberId(), club.getClubId());

        // Then: 이체 후 잔액과 히스토리 검증
        AdminAccount updatedAdminAccount = adminAccountRepository.findById(adminAccount.getAdminAccountId()).orElse(null);
        assertThat(updatedAdminAccount).isNotNull();
        assertThat(updatedAdminAccount.getAdminAccountAmount()).isEqualTo(9990000L); // 잔액 확인

        AdminAccountHistory history = adminAccountHistoryRepository.findAll().get(0);
        assertThat(history).isNotNull();
        assertThat(history.getAdminAccountHistoryTranAmount()).isEqualTo(10000L); // 이체 금액 확인
        assertThat(history.getAdminAccountHistoryContent()).isEqualTo("테스트 동아리 회비 이체 테스트 회원의 회비");
    }

    @DisplayName("농협 계좌 이체가 성공적으로 이루어진다.")
    @Test
    @Disabled
    void transferNHBankTest() {
        // Given
        Club club = clubRepository.save(Club.builder()
                .clubName("테스트 동아리")
                .clubEnglishName("ATC")
                .clubDues(10000)
                .build());

        ClubAccount clubAccount = clubAccountRepository.save(ClubAccount.builder()
                .club(club)
                .clubAccountNumber("3020000011656")
                .clubAccountBankCode("011")
                .clubAccountPinTechNumber("000")
                .clubAccountBankName("농협은행")
                .build());

        Member member = memberRepository.save(Member.builder()
                .memberName("테스트 회원")
                .memberEmail("test@ajou.ac.kr")
                .memberProvideId("1")
                .build());

        AdminAccount adminAccount = adminAccountRepository.findById(1L).get();

        // When: 이체 서비스 호출
        mockBankService.transferClubFee(member.getMemberId(), club.getClubId());

        // Then: 이체 후 잔액과 히스토리 검증
        AdminAccount updatedAdminAccount = adminAccountRepository.findById(adminAccount.getAdminAccountId()).orElse(null);
        assertThat(updatedAdminAccount).isNotNull();
        assertThat(updatedAdminAccount.getAdminAccountAmount()).isEqualTo(9990000L); // 잔액 확인

        AdminAccountHistory history = adminAccountHistoryRepository.findAll().get(0);
        assertThat(history).isNotNull();
        assertThat(history.getAdminAccountHistoryTranAmount()).isEqualTo(10000L); // 이체 금액 확인
        assertThat(history.getAdminAccountHistoryContent()).isEqualTo("테스트 동아리 회비 이체 테스트 회원의 회비");
    }
}