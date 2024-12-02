package woohakdong.server.domain.groupmember;

import java.util.List;
import woohakdong.server.domain.group.Group;

public interface GroupMemberRepository {
    List<GroupMember> getByGroup(Group group);
}
