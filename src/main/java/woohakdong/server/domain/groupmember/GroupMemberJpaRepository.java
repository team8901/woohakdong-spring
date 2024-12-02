package woohakdong.server.domain.groupmember;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import woohakdong.server.domain.clubmember.ClubMember;
import woohakdong.server.domain.group.Group;
import woohakdong.server.domain.member.Member;

public interface GroupMemberJpaRepository extends JpaRepository<GroupMember, Long> {
    List<GroupMember> findAllByGroup(Group group);

    boolean existsByGroupAndClubMember(Group group, ClubMember clubMember);
}
