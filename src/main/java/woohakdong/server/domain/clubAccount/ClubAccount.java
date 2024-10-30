package woohakdong.server.domain.clubAccount;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.clubAccountHistory.ClubAccountHistory;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ClubAccount {

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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id")
    private Club club;

    @OneToMany(mappedBy = "clubAccount", cascade = CascadeType.ALL)
    private List<ClubAccountHistory> clubAccountHistorys;

    @Builder
    public ClubAccount(Club club, String clubAccountBankName, String clubAccountNumber,
                       String clubAccountPinTechNumber, String clubAccountBankCode, LocalDateTime clubAccountLastUpdateDate) {
        this.club = club;
        this.clubAccountBankName = clubAccountBankName;
        this.clubAccountNumber = clubAccountNumber;
        this.clubAccountPinTechNumber = clubAccountPinTechNumber;
        this.clubAccountBankCode = clubAccountBankCode;
        this.clubAccountLastUpdateDate = clubAccountLastUpdateDate;
    }

    public void setClubAccountLastUpdateDate(LocalDateTime clubAccountLastUpdateDate) {
        this.clubAccountLastUpdateDate = clubAccountLastUpdateDate;
    }
}
