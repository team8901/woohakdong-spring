package woohakdong.server.domain.group;

import static woohakdong.server.common.exception.CustomErrorInfo.GROUP_MEMBER_LIMIT_EXCEEDED;
import static woohakdong.server.domain.group.GroupType.CLUB_PAYMENT;
import static woohakdong.server.domain.group.GroupType.EVENT;

import jakarta.persistence.CascadeType;
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
import woohakdong.server.common.exception.CustomErrorInfo;
import woohakdong.server.common.exception.CustomException;
import woohakdong.server.domain.BaseEntity;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.clubmember.ClubMember;
import woohakdong.server.domain.groupmember.GroupMember;
import woohakdong.server.domain.member.Member;
import woohakdong.server.domain.order.Order;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "\"group\"")
public class Group extends BaseEntity {

    private static final Integer PER_MEMBER_FEE = 500;
    private static final Integer BASE_SERVICE_FEE = 30000;
    private static final String BASE_SERVER_URL = "https://www.woohakdong.com/clubs/%s/groups/%s";

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

    @Column(nullable = false)
    private Integer groupMemberLimit;

    private Integer groupMemberCount;

    @Column(nullable = false)
    private Boolean groupIsActivated;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id")
    private Club club;

    @OneToMany(mappedBy = "group")
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "group", cascade = CascadeType.MERGE)
    private List<GroupMember> groupMembers = new ArrayList<>();

    @Builder
    private Group(String groupName, String groupDescription, Integer groupAmount, String groupJoinLink,
                  String groupChatLink, String groupChatPassword, GroupType groupType, Club club,
                  Boolean groupIsActivated, Integer groupMemberLimit, Integer groupMemberCount) {
        this.groupName = groupName;
        this.groupDescription = groupDescription;
        this.groupAmount = groupAmount;
        this.groupJoinLink = groupJoinLink;
        this.groupChatLink = groupChatLink;
        this.groupChatPassword = groupChatPassword;
        this.groupType = groupType;
        this.club = club;
        this.groupIsActivated = groupIsActivated;
        this.groupMemberLimit = groupMemberLimit;
        this.groupMemberCount = groupMemberCount;
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
                .groupIsActivated(true)
                .groupMemberLimit(999)
                .groupMemberCount(0)
                .build();
    }

    public static Group createClubPaymentGroup(Club club, Integer clubMemberCount) {
        return Group.builder()
                .groupName((club.getClubName() + "의 우학동 서비스 사용료 결제").substring(0, 15))
                .groupDescription(club.getClubName() + "동아리의 우학동 서비스 사용료는 " + clubMemberCount + " * 500원 입니다.")
                .groupAmount(BASE_SERVICE_FEE + clubMemberCount * PER_MEMBER_FEE)
                .groupJoinLink(null)
                .groupChatLink(null)
                .groupChatPassword(null)
                .groupType(CLUB_PAYMENT)
                .club(club)
                .groupIsActivated(true)
                .groupMemberLimit(1)
                .groupMemberCount(0)
                .build();
    }

    public static Group createEventGroup(Club club, String groupName, String groupDescription, Integer groupAmount,
                                         String groupChatLink, String groupChatPassword, Integer groupMemberLimit) {
        return Group.builder()
                .groupName(groupName)
                .groupDescription(groupDescription)
                .groupAmount(groupAmount)
                .groupJoinLink(null)
                .groupChatLink(groupChatLink)
                .groupChatPassword(groupChatPassword)
                .groupType(EVENT)
                .club(club)
                .groupIsActivated(true)
                .groupMemberLimit(groupMemberLimit)
                .groupMemberCount(0)
                .build();
    }

    public void updateJoinGroup(Integer groupAmount, String groupChatLink, String groupChatPassword) {
        this.groupAmount = groupAmount;
        this.groupChatLink = groupChatLink;
        this.groupChatPassword = groupChatPassword;
    }

    public boolean isTypeOf(GroupType groupType) {
        return this.groupType == groupType;
    }

    public void setGroupJoinLink() {
        this.groupJoinLink = String.format(BASE_SERVER_URL, club.getClubEnglishName(), groupId);
    }

    public void updateEventGroup(String groupName, String groupDescription, String groupChatLink,
                                 String groupChatPassword, Boolean groupIsActivated, Integer groupMemberLimit) {
        if ( groupMemberLimit < this.groupMemberCount ) {
            throw new CustomException(GROUP_MEMBER_LIMIT_EXCEEDED);
        }
        this.groupName = groupName;
        this.groupDescription = groupDescription;
        this.groupChatLink = groupChatLink;
        this.groupChatPassword = groupChatPassword;
        this.groupIsActivated = groupIsActivated;
        this.groupMemberLimit = groupMemberLimit;
    }

    public void changeAvailability() {
        this.groupIsActivated = !this.groupIsActivated;
    }

    public void joinNewMember(ClubMember clubMember) {
        if ( this.groupMemberCount + 1 > this.groupMemberLimit ) {
            throw new CustomException(CustomErrorInfo.GROUP_MEMBER_LIMIT_EXCEEDED);
        }
        this.groupMemberCount++;
        this.groupMembers.add(GroupMember.create(clubMember, this));
    }
}
