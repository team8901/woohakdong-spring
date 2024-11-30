package woohakdong.server.domain.groupmember;

import jakarta.persistence.Entity;
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
import woohakdong.server.domain.BaseEntity;
import woohakdong.server.domain.clubmember.ClubMember;
import woohakdong.server.domain.group.Group;
import woohakdong.server.domain.member.Member;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class GroupMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupMemberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_member_id")
    private ClubMember clubMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @Builder
    public GroupMember(ClubMember clubMember, Group group) {
        this.clubMember = clubMember;
        this.group = group;
    }

    public static GroupMember create(ClubMember clubMember, Group group) {
        return GroupMember.builder()
                .clubMember(clubMember)
                .group(group)
                .build();
    }
}
