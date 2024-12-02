package woohakdong.server.api.service.dues;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import woohakdong.server.SecurityContextSetup;
import woohakdong.server.domain.admin.adminAccount.AccountType;
import woohakdong.server.domain.clubAccount.ClubAccount;
import woohakdong.server.domain.clubAccount.ClubAccountRepository;
import woohakdong.server.domain.clubAccountHistory.ClubAccountHistory;
import woohakdong.server.domain.clubAccountHistory.ClubAccountHistoryRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DuesServiceTest extends SecurityContextSetup {
    @Autowired
    private ClubAccountHistoryRepository clubAccountHistoryRepository;

    @Autowired
    private ClubAccountRepository clubAccountRepository;

    @Test
    @DisplayName("특정 클럽의 월별 거래 내역을 조회할 수 있다")
    void findMonthlyTransactions() {
        // given
        ClubAccount clubAccount = clubAccountRepository.save(ClubAccount.builder()
                .clubAccountBankName("농협은행")
                .clubAccountNumber("123456789")
                .clubAccountPinTechNumber("987654321")
                .clubAccountBankCode("011")
                .build());

        clubAccountHistoryRepository.save(new ClubAccountHistory(AccountType.DEPOSIT, LocalDateTime.of(2024, 10, 15, 10, 0), 100000L, 10000L, "Test Deposit 1", clubAccount));
        clubAccountHistoryRepository.save(new ClubAccountHistory(AccountType.WITHDRAW, LocalDateTime.of(2024, 10, 20, 15, 30), 90000L, 10000L, "Test Withdraw", clubAccount));
        clubAccountHistoryRepository.save(new ClubAccountHistory(AccountType.DEPOSIT, LocalDateTime.of(2024, 11, 5, 11, 0), 100000L, 10000L, "Test Deposit 2", clubAccount));

        LocalDate date = LocalDate.of(2024, 10, 1);

        // when
        List<ClubAccountHistory> histories = clubAccountHistoryRepository.findMonthlyTransactions(clubAccount, 2024, 10);

        // then
        assertThat(histories).hasSize(2);
        assertThat(histories).extracting("clubAccountHistoryContent").containsExactlyInAnyOrder("Test Deposit 1", "Test Withdraw");
    }
}