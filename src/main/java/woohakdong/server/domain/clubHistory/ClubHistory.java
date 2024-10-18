package woohakdong.server.domain.clubHistory;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import woohakdong.server.domain.club.Club;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClubHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clubHistoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id")
    private Club club;

    @Column(nullable = false)
    private LocalDate clubHistoryUsageDate;
}
