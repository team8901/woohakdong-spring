package woohakdong.server.domain.admin.adminAccountHistory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import woohakdong.server.domain.BaseEntity;
import woohakdong.server.domain.admin.adminAccount.AccountType;
import woohakdong.server.domain.admin.adminAccount.AdminAccount;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class AdminAccountHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adminAccountHistoryId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountType adminAccountHistoryInOutType;

    @Column(nullable = false)
    private LocalDate adminAccountHistoryTranDate;

    @Column(nullable = false)
    private Long adminAccountHistoryBalanceAmount;

    @Column(nullable = false)
    private Long adminAccountHistoryTranAmount;

    @Column(nullable = false)
    private String adminAccountHistoryContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_account_id")
    private AdminAccount adminAccount;

    @Builder
    private AdminAccountHistory(AccountType adminAccountHistoryInOutType, LocalDate adminAccountHistoryTranDate,
                                Long adminAccountHistoryBalanceAmount, Long adminAccountHistoryTranAmount,
                                String adminAccountHistoryContent, AdminAccount adminAccount) {
        this.adminAccountHistoryInOutType = adminAccountHistoryInOutType;
        this.adminAccountHistoryTranDate = adminAccountHistoryTranDate;
        this.adminAccountHistoryBalanceAmount = adminAccountHistoryBalanceAmount;
        this.adminAccountHistoryTranAmount = adminAccountHistoryTranAmount;
        this.adminAccountHistoryContent = adminAccountHistoryContent;
        this.adminAccount = adminAccount;
    }

    public static AdminAccountHistory create(AccountType adminAccountHistoryInOutType,
                                             LocalDate adminAccountHistoryTranDate,
                                             Long adminAccountHistoryBalanceAmount, Long adminAccountHistoryTranAmount,
                                             AdminAccount adminAccount, String adminAccountHistoryContent) {
        return AdminAccountHistory.builder()
                .adminAccountHistoryInOutType(adminAccountHistoryInOutType)
                .adminAccountHistoryTranDate(adminAccountHistoryTranDate)
                .adminAccountHistoryBalanceAmount(adminAccountHistoryBalanceAmount)
                .adminAccountHistoryTranAmount(adminAccountHistoryTranAmount)
                .adminAccountHistoryContent(adminAccountHistoryContent)
                .adminAccount(adminAccount)
                .build();
    }
}
