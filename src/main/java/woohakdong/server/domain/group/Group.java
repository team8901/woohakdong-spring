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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import woohakdong.server.domain.BaseEntity;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.order.Order;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "\"group\"")
public class Group extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupId;

    @Column(nullable = false)
    private String groupName;

    private String groupDescription;

    private Integer groupAmount;

    @Column(nullable = false)
    private String groupJoinLink;

    @Enumerated(EnumType.STRING)
    private GroupType groupType;

    private String groupChatLink;

    private String groupChatPassword;

    private Boolean groupIsAvailable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id")
    private Club club;

    @OneToMany(mappedBy = "group")
    private List<Order> orders = new ArrayList<>();

    @Builder
    private Group(String groupName, String groupDescription, Integer groupAmount, String groupJoinLink,
                  String groupChatLink, String groupChatPassword, GroupType groupType, Club club) {
        this.groupName = groupName;
        this.groupDescription = groupDescription;
        this.groupAmount = groupAmount;
        this.groupJoinLink = groupJoinLink;
        this.groupChatLink = groupChatLink;
        this.groupChatPassword = groupChatPassword;
        this.groupIsAvailable = true;
        this.groupType = groupType;
        this.club = club;
    }

    public static Group create(String groupName, String groupDescription, Integer groupAmount, String groupJoinLink,
                               String groupChatLink, String groupChatPassword, GroupType groupType, Club club) {
        return Group.builder()
                .groupName(groupName)
                .groupDescription(groupDescription)
                .groupAmount(groupAmount)
                .groupJoinLink(groupJoinLink)
                .groupChatLink(groupChatLink)
                .groupChatPassword(groupChatPassword)
                .groupType(groupType)
                .club(club)
                .build();
    }

    public void updateJoinGroup(String groupDescription, Integer groupAmount, String groupChatLink,
                                String groupChatPassword) {
        this.groupDescription = groupDescription;
        this.groupAmount = groupAmount;
        this.groupChatLink = groupChatLink;
        this.groupChatPassword = groupChatPassword;
    }

    public void disableGroup() {
        this.groupIsAvailable = false;
    }

    public boolean isTypeOf(GroupType groupType) {
        return this.groupType == groupType;
    }
}
