package woohakdong.server.domain.admin.adminAccount;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import woohakdong.server.domain.BaseEntity;
import woohakdong.server.domain.admin.adminAccountHistory.AdminAccountHistory;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class AdminAccount extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adminAccountId;

    @Column(nullable = false)
    private String adminAccountBankName;

    @Column(nullable = false)
    private String adminAccountNumber;

    @Column(nullable = false)
    private Long adminAccountAmount;

    @Column(nullable = false)
    private String adminAccountBankCode;

    @OneToMany(mappedBy = "adminAccount", cascade = CascadeType.ALL)
    private List<AdminAccountHistory> adminAccountHistorys;

    @Builder
    private AdminAccount(String adminAccountBankName, String adminAccountNumber,
                         Long adminAccountAmount, String adminAccountBankCode) {
        this.adminAccountBankName = adminAccountBankName;
        this.adminAccountNumber = adminAccountNumber;
        this.adminAccountAmount = adminAccountAmount;
        this.adminAccountBankCode = adminAccountBankCode;
    }

    public static AdminAccount create(String adminAccountBankName, String adminAccountNumber,
                                      Long adminAccountAmount, String adminAccountBankCode) {
        return AdminAccount.builder()
                .adminAccountBankName(adminAccountBankName)
                .adminAccountNumber(adminAccountNumber)
                .adminAccountAmount(adminAccountAmount)
                .adminAccountBankCode(adminAccountBankCode)
                .build();
    }

    public void setAdminAccountAmount(Long adminAccountAmount) {
        this.adminAccountAmount = adminAccountAmount;
    }
}
