package woohakdong.server.domain.group;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import woohakdong.server.common.exception.CustomErrorInfo;
import woohakdong.server.common.exception.CustomException;
import woohakdong.server.domain.club.Club;

@RequiredArgsConstructor
@Repository
public class GroupRepositoryImpl implements GroupRepository {

    private final GroupJpaRepository groupJpaRepository;

    @Override
    public Group save(Group group) {
        return groupJpaRepository.save(group);
    }

    @Override
    public Group getById(Long groupId) {
        return groupJpaRepository.findById(groupId)
                .orElseThrow(() -> new CustomException(CustomErrorInfo.GROUP_NOT_FOUND));
    }

    @Override
    public List<Group> getAll() {
        return groupJpaRepository.findAll();
    }


    @Override
    public Group getByClubAndGroupTypeAndGroupIsAvailable(Club club, GroupType groupType, Boolean groupIsAvailable) {
        return groupJpaRepository.findByClubAndGroupTypeAndGroupIsAvailable(club, groupType, groupIsAvailable)
                .orElseThrow(() -> new CustomException(CustomErrorInfo.GROUP_NOT_FOUND));
    }

    @Override
    public List<Group> getAllByClubAndGroupType(Club club, GroupType groupType) {
        return groupJpaRepository.findAllByClubAndGroupType(club, groupType);
    }
}
