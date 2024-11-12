package woohakdong.server.domain.clubAccountHistory;


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
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import woohakdong.server.domain.BaseEntity;
import woohakdong.server.domain.admin.adminAccount.AccountType;
import woohakdong.server.domain.clubAccount.ClubAccount;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ClubAccountHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clubAccountHistoryId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountType clubAccountHistoryInOutType;

    @Column(nullable = false)
    private LocalDateTime clubAccountHistoryTranDate;

    @Column(nullable = false)
    private Long clubAccountHistoryBalanceAmount;

    @Column(nullable = false)
    private Long clubAccountHistoryTranAmount;

    @Column(nullable = false)
    private String clubAccountHistoryContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_account_id")
    private ClubAccount clubAccount;

    @Builder
    public ClubAccountHistory(AccountType clubAccountHistoryInOutType, LocalDateTime clubAccountHistoryTranDate,
                              Long clubAccountHistoryBalanceAmount, Long clubAccountHistoryTranAmount,
                              String clubAccountHistoryContent, ClubAccount clubAccount) {
        this.clubAccountHistoryInOutType = clubAccountHistoryInOutType;
        this.clubAccountHistoryTranDate = clubAccountHistoryTranDate;
        this.clubAccountHistoryBalanceAmount = clubAccountHistoryBalanceAmount;
        this.clubAccountHistoryTranAmount = clubAccountHistoryTranAmount;
        this.clubAccountHistoryContent = clubAccountHistoryContent;
        this.clubAccount = clubAccount;
    }
}
