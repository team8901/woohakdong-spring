package woohakdong.server.domain.clubAccount;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import woohakdong.server.domain.club.Club;

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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id")
    private Club club;

    @Builder
    public ClubAccount(Club club, String clubAccountBankName, String clubAccountNumber,
                       String clubAccountPinTechNumber) {
        this.club = club;
        this.clubAccountBankName = clubAccountBankName;
        this.clubAccountNumber = clubAccountNumber;
        this.clubAccountPinTechNumber = clubAccountPinTechNumber;
    }
}
