package woohakdong.server.domain.groupmember;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import woohakdong.server.domain.group.Group;

public interface GroupMemberJpaRepository extends JpaRepository<GroupMember, Long> {
    List<GroupMember> findAllByGroup(Group group);
}
