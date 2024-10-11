package woohakdong.server.api.service.bank;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static woohakdong.server.common.exception.CustomErrorInfo.BANK_INVALID_ACCOUNT_NUMBER;
import static woohakdong.server.common.exception.CustomErrorInfo.BANK_NOT_SUPPORTED;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import woohakdong.server.api.controller.club.dto.ClubAccountValidateRequest;
import woohakdong.server.api.controller.club.dto.ClubAccountValidateResponse;
import woohakdong.server.common.exception.CustomException;

class MockBankServiceTest {

    private final MockBankService mockBankService = new MockBankService();

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
}