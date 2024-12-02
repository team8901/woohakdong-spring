package woohakdong.server.domain.groupmember;

import java.util.List;
import woohakdong.server.domain.clubmember.ClubMember;
import woohakdong.server.domain.group.Group;
import woohakdong.server.domain.member.Member;

public interface GroupMemberRepository {
    GroupMember save(GroupMember groupMember);

    List<GroupMember> getByGroup(Group group);

    boolean checkAlreadyJoined(Group group, ClubMember clubMember);
}
