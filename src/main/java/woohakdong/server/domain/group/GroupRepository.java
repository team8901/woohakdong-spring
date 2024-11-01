package woohakdong.server.domain.group;

import java.util.List;
import woohakdong.server.domain.club.Club;

public interface GroupRepository {
    Group save(Group group);

    Group getById(Long groupId);

    List<Group> getAll();

    Group getByClubAndGroupTypeAndGroupIsAvailable(Club club, GroupType groupType, Boolean groupIsAvailable);

    List<Group> getAllByClubAndGroupType(Club club, GroupType groupType);
}
