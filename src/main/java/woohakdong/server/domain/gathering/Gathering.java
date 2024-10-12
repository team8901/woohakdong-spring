package woohakdong.server.domain.gathering;

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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.clubmember.ClubMember;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Gathering {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gatheringId;

    @Column(nullable = false)
    private String gatheringName;

    private String gatheringDescription;

    private int gatheringAmount;

    @Column(nullable = false)
    private String gatheringLink;

    @Enumerated(EnumType.STRING)
    private GatheringType gatheringType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id")
    private Club club;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_member_id")
    private ClubMember clubMember;

    @Builder
    public Gathering(String gatheringName, String gatheringDescription, int gatheringAmount, String gatheringLink,
                     GatheringType gatheringType, Club club) {
        this.gatheringName = gatheringName;
        this.gatheringDescription = gatheringDescription;
        this.gatheringAmount = gatheringAmount;
        this.gatheringLink = gatheringLink;
        this.gatheringType = gatheringType;
        this.club = club;
    }
}
