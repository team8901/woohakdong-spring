package woohakdong.server.domain.clubAccount;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import woohakdong.server.domain.BaseEntity;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.clubAccountHistory.ClubAccountHistory;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ClubAccount extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clubAccountId;

    @Column(nullable = false)
    private String clubAccountBankName;

    @Column(nullable = false)
    private String clubAccountNumber;

    @Column(nullable = false)
    private String clubAccountPinTechNumber;

    @Column
    private LocalDateTime clubAccountLastUpdateDate;

    @Column(nullable = false)
    private String clubAccountBankCode;

    private Long clubAccountBalance;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id")
    private Club club;

    @OneToMany(mappedBy = "clubAccount", cascade = CascadeType.ALL)
    private List<ClubAccountHistory> clubAccountHistorys;

    @Builder
    public ClubAccount(Club club, String clubAccountBankName, String clubAccountNumber, String clubAccountPinTechNumber,
                       String clubAccountBankCode, LocalDateTime clubAccountLastUpdateDate, Long clubAccountBalance) {
        this.club = club;
        this.clubAccountBankName = clubAccountBankName;
        this.clubAccountNumber = clubAccountNumber;
        this.clubAccountPinTechNumber = clubAccountPinTechNumber;
        this.clubAccountBankCode = clubAccountBankCode;
        this.clubAccountLastUpdateDate = clubAccountLastUpdateDate;
        this.clubAccountBalance = clubAccountBalance;
    }

    public static ClubAccount create(Club club, String bankName, String accountNumber, String pinTechNumber,
                                     String bankCode, LocalDateTime localDateTime) {
        return ClubAccount.builder()
                .club(club)
                .clubAccountBankName(bankName)
                .clubAccountNumber(accountNumber)
                .clubAccountPinTechNumber(pinTechNumber)
                .clubAccountBankCode(bankCode)
                .clubAccountLastUpdateDate(localDateTime)
                .clubAccountBalance(0L)
                .build();
    }

    public void setClubAccountLastUpdateDate(LocalDateTime clubAccountLastUpdateDate) {
        this.clubAccountLastUpdateDate = clubAccountLastUpdateDate;
    }

    public void setClubAccountBalance(Long clubAccountBalance) {
        this.clubAccountBalance = clubAccountBalance;
    }
}
