package woohakdong.server.domain.groupmember;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import woohakdong.server.domain.clubmember.ClubMember;
import woohakdong.server.domain.group.Group;

@RequiredArgsConstructor
@Repository
public class GroupMemberRepositoryImpl implements GroupMemberRepository {

    private final GroupMemberJpaRepository groupMemberJpaRepository;

    @Override
    public GroupMember save(GroupMember groupMember) {
        return groupMemberJpaRepository.save(groupMember);
    }

    @Override
    public List<GroupMember> getAllByGroup(Group group) {
        return groupMemberJpaRepository.findAllByGroup(group);
    }

    @Override
    public boolean checkAlreadyJoined(Group group, ClubMember clubMember) {
        return groupMemberJpaRepository.existsByGroupAndClubMember(group, clubMember);
    }
}
