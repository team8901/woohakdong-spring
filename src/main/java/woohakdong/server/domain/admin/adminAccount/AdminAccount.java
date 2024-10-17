package woohakdong.server.domain.admin.adminAccount;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import woohakdong.server.domain.admin.adminAccountHistory.AdminAccountHistory;

import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class AdminAccount {
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
    public AdminAccount(String adminAccountBankName, String adminAccountNumber,
                        Long adminAccountAmount, String adminAccountBankCode) {
        this.adminAccountBankName = adminAccountBankName;
        this.adminAccountNumber = adminAccountNumber;
        this.adminAccountAmount = adminAccountAmount;
        this.adminAccountBankCode = adminAccountBankCode;
    }

    public void setAdminAccountAmount(Long adminAccountAmount) {
        this.adminAccountAmount = adminAccountAmount;
    }
}
