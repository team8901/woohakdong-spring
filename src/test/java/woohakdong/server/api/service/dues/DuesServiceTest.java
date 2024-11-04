package woohakdong.server.api.service.dues;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import woohakdong.server.api.service.bank.MockBankService;
import woohakdong.server.domain.admin.adminAccount.AccountType;
import woohakdong.server.domain.clubAccount.ClubAccount;
import woohakdong.server.domain.clubAccount.ClubAccountRepository;
import woohakdong.server.domain.clubAccountHistory.ClubAccountHistory;
import woohakdong.server.domain.clubAccountHistory.ClubAccountHistoryRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class DuesServiceTest {
    @Autowired
    private ClubAccountHistoryRepository clubAccountHistoryRepository;

    @Autowired
    private ClubAccountRepository clubAccountRepository;

    @Autowired
    private MockBankService mockBankService;

    @Test
    @DisplayName("특정 클럽의 월별 거래 내역을 조회할 수 있다")
    void findMonthlyTransactions() {
        // given
        ClubAccount clubAccount = clubAccountRepository.save(new ClubAccount(null, "Test Bank", "1234567890", "0011223344", "011", LocalDateTime.now()));

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

    @Test
    void updateTransaction() {
        mockBankService.fetchTransactions()
    }
}