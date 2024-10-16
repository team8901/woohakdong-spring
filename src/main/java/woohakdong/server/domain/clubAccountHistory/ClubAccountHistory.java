package woohakdong.server.domain.clubAccountHistory;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import woohakdong.server.domain.admin.adminAccount.AccountType;
import woohakdong.server.domain.clubAccount.ClubAccount;

import java.time.LocalDate;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ClubAccountHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clubAccountHistoryId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountType clubAccountHistoryInOutType;

    @Column(nullable = false)
    private LocalDate clubAccountHistoryTranDate;

    @Column(nullable = false)
    private Long clubAccountHistoryBalanceAmt;

    @Column(nullable = false)
    private Long clubAccountHistoryTranAmt;

    @Column(nullable = false)
    private String clubAccountHistoryContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_account_id")
    private ClubAccount clubAccount;

    @Builder
    public ClubAccountHistory(AccountType clubAccountHistoryInOutType, LocalDate clubAccountHistoryTranDate,
                              Long clubAccountHistoryBalanceAmt, Long clubAccountHistoryTranAmt,
                              String clubAccountHistoryContent, ClubAccount clubAccount) {
        this.clubAccountHistoryInOutType = clubAccountHistoryInOutType;
        this.clubAccountHistoryTranDate = clubAccountHistoryTranDate;
        this.clubAccountHistoryBalanceAmt = clubAccountHistoryBalanceAmt;
        this.clubAccountHistoryTranAmt = clubAccountHistoryTranAmt;
        this.clubAccountHistoryContent = clubAccountHistoryContent;
        this.clubAccount = clubAccount;
    }
}
