package woohakdong.server.domain.admin.adminAccountHistory;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import woohakdong.server.domain.admin.adminAccount.AccountType;
import woohakdong.server.domain.admin.adminAccount.AdminAccount;
import woohakdong.server.domain.clubAccount.ClubAccount;

import java.time.LocalDate;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class AdminAccountHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adminAccountHistoryId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountType adminAccountHistoryInOutType;

    @Column(nullable = false)
    private LocalDate adminAccountHistoryTranDate;

    @Column(nullable = false)
    private Long adminAccountHistoryBalanceAmt;

    @Column(nullable = false)
    private Long adminAccountHistoryTranAmt;

    @Column(nullable = false)
    private String adminAccountHistoryContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_account_id")
    private AdminAccount adminAccount;

    @Builder
    public AdminAccountHistory(AccountType adminAccountHistoryInOutType, LocalDate adminAccountHistoryTranDate,
                               Long adminAccountHistoryBalanceAmt, Long adminAccountHistoryTranAmt,
                               String adminAccountHistoryContent, AdminAccount adminAccount) {
        this.adminAccountHistoryInOutType = adminAccountHistoryInOutType;
        this.adminAccountHistoryTranDate = adminAccountHistoryTranDate;
        this.adminAccountHistoryBalanceAmt = adminAccountHistoryBalanceAmt;
        this.adminAccountHistoryTranAmt = adminAccountHistoryTranAmt;
        this.adminAccountHistoryContent = adminAccountHistoryContent;
        this.adminAccount = adminAccount;
    }
}
