package woohakdong.server.domain.group;

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
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.clubmember.ClubMember;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "groups")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupId;

    @Column(nullable = false)
    private String groupName;

    private String groupDescription;

    private int groupAmount;

    @Column(nullable = false)
    private String groupLink;

    @Enumerated(EnumType.STRING)
    private GroupType groupType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id")
    private Club club;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_member_id")
    private ClubMember clubMember;

    @Builder
    public Group(String groupName, String groupDescription, int groupAmount, String groupLink,
                 GroupType groupType, Club club) {
        this.groupName = groupName;
        this.groupDescription = groupDescription;
        this.groupAmount = groupAmount;
        this.groupLink = groupLink;
        this.groupType = groupType;
        this.club = club;
    }
}
