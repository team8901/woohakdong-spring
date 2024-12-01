package woohakdong.server.domain.groupmember;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import woohakdong.server.domain.group.Group;

@RequiredArgsConstructor
@Repository
public class GroupMemberRepositoryImpl implements GroupMemberRepository {

    private final GroupMemberJpaRepository groupMemberJpaRepository;

    @Override
    public List<GroupMember> getByGroup(Group group) {
        return groupMemberJpaRepository.findAllByGroup(group);
    }

    @Override
    public Integer countByGroup(Group group) {
        return groupMemberJpaRepository.countByGroup(group);
    }

    @Override
    public void flush() {
        groupMemberJpaRepository.flush();
    }
}
