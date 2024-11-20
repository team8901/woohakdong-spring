package woohakdong.server.domain.group;

import static woohakdong.server.domain.group.GroupType.CLUB_PAYMENT;

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

    private static final Integer PER_MEMBER_FEE = 500;
    private static final Integer BASE_SERVICE_FEE = 30000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupId;

    @Column(nullable = false)
    private String groupName;

    private String groupDescription;

    private Integer groupAmount;

    private String groupJoinLink;

    @Enumerated(EnumType.STRING)
    private GroupType groupType;

    private String groupChatLink;

    private String groupChatPassword;

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

    public static Group createClubPaymentGroup(Club club, Integer clubMemberCount) {
        System.out.println("clubMemberCount = " + clubMemberCount);
        return Group.builder()
                .groupName(club.getClubName() + " 동아리의 우학동 서비스 사용료 결제")
                .groupDescription(club.getClubName() + "동아리의 우학동 서비스 사용료는 " + clubMemberCount + " * 500원 입니다.")
                .groupAmount(BASE_SERVICE_FEE + clubMemberCount * PER_MEMBER_FEE)
                .groupJoinLink(null)
                .groupChatLink(null)
                .groupChatPassword(null)
                .groupType(CLUB_PAYMENT)
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

    public boolean isTypeOf(GroupType groupType) {
        return this.groupType == groupType;
    }
}
