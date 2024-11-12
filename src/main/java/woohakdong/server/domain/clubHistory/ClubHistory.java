package woohakdong.server.domain.clubHistory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import woohakdong.server.domain.club.Club;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ClubHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clubHistoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id")
    private Club club;

    @Column(nullable = false)
    private LocalDate clubHistoryUsageDate;

    @Builder
    public ClubHistory(Club club, LocalDate clubHistoryUsageDate) {
        this.club = club;
        this.clubHistoryUsageDate = clubHistoryUsageDate;
    }

    public static ClubHistory create(Club club, LocalDate assignedTerm) {
        return ClubHistory.builder()
                .club(club)
                .clubHistoryUsageDate(assignedTerm)
                .build();
    }
}
