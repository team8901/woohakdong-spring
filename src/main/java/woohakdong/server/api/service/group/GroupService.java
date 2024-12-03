package woohakdong.server.api.service.group;

import static woohakdong.server.domain.clubmember.ClubMemberRole.OFFICER;
import static woohakdong.server.domain.group.GroupType.EVENT;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woohakdong.server.api.controller.clubMember.dto.ClubMemberInfoResponse;
import woohakdong.server.api.controller.group.dto.GroupCreateRequest;
import woohakdong.server.api.controller.group.dto.GroupIdResponse;
import woohakdong.server.api.controller.group.dto.GroupInfoResponse;
import woohakdong.server.api.controller.group.dto.GroupUpdateRequest;
import woohakdong.server.common.util.date.DateUtil;
import woohakdong.server.common.util.security.SecurityUtil;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.club.ClubRepository;
import woohakdong.server.domain.clubmember.ClubMember;
import woohakdong.server.domain.clubmember.ClubMemberRepository;
import woohakdong.server.domain.group.Group;
import woohakdong.server.domain.group.GroupRepository;
import woohakdong.server.domain.groupmember.GroupMember;
import woohakdong.server.domain.groupmember.GroupMemberRepository;
import woohakdong.server.domain.member.Member;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final SecurityUtil securityUtil;
    private final DateUtil dateUtil;
    private final RedissonClient redissonClient;

    private final GroupRepository groupRepository;
    private final ClubRepository clubRepository;
    private final ClubMemberRepository clubMemberRepository;
    private final GroupServiceTrans groupServiceTrans;
    private final GroupMemberRepository groupMemberRepository;

    @Transactional
    public GroupIdResponse createEventGroup(Long clubId, GroupCreateRequest request, LocalDate date) {
        Member member = securityUtil.getMember();
        Club club = clubRepository.getById(clubId);

        ClubMember clubMember = clubMemberRepository.getByClubAndMemberAndAssignedTerm(club, member,
                dateUtil.getAssignedTerm(date));
        clubMember.hasAuthorityOf(OFFICER);

        Group group = Group.createEventGroup(club, request.groupName(), request.groupDescription(),
                request.groupAmount(), request.groupChatLink(), request.groupChatPassword(),
                request.groupMemberLimit());
        Group saved = groupRepository.save(group);
        saved.setGroupJoinLink();

        return GroupIdResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public GroupInfoResponse findOneGroup(Long groupId, LocalDate date) {
        Member member = securityUtil.getMember();
        Group group = groupRepository.getById(groupId);
        Club club = group.getClub();

        // 클럽에 속한 멤버인지 확인
        clubMemberRepository.getByClubAndMemberAndAssignedTerm(club, member, dateUtil.getAssignedTerm(date));

        return GroupInfoResponse.from(group);
    }

    @Transactional(readOnly = true)
    public List<GroupInfoResponse> findAllEventGroupOfClub(Long clubId) {
        Club club = clubRepository.getById(clubId);
        List<Group> groups = groupRepository.getAllByClubAndGroupType(club, EVENT);

        return groups.stream()
                .map(GroupInfoResponse::from)
                .toList();
    }

    @Transactional
    public GroupIdResponse updateGroupInfo(Long groupId, GroupUpdateRequest request, LocalDate date) {
        Member member = securityUtil.getMember();
        Group group = groupRepository.getById(groupId);
        Club club = group.getClub();

        // 클럽에 속한 멤버인지 확인
        ClubMember clubMember = clubMemberRepository.getByClubAndMemberAndAssignedTerm(club, member,
                dateUtil.getAssignedTerm(date));
        clubMember.hasAuthorityOf(OFFICER);

        group.updateEventGroup(request.groupName(), request.groupDescription(), request.groupChatLink(),
                request.groupChatPassword(), request.groupIsActivated(), request.groupMemberLimit());

        return GroupIdResponse.from(group);
    }

    @Transactional
    public void deleteGroup(Long groupId, LocalDate date) {
        Member member = securityUtil.getMember();
        Group group = groupRepository.getById(groupId);
        Club club = group.getClub();

        // 클럽에 속한 멤버인지 확인
        ClubMember clubMember = clubMemberRepository.getByClubAndMemberAndAssignedTerm(club, member,
                dateUtil.getAssignedTerm(date));
        clubMember.hasAuthorityOf(OFFICER);

        groupRepository.delete(group);
    }

    @Transactional
    public void changeAvailabilityOfGroup(Long groupId, LocalDate date) {
        Member member = securityUtil.getMember();
        Group group = groupRepository.getById(groupId);
        Club club = group.getClub();

        // 클럽에 속한 멤버인지 확인
        ClubMember clubMember = clubMemberRepository.getByClubAndMemberAndAssignedTerm(club, member,
                dateUtil.getAssignedTerm(date));
        clubMember.hasAuthorityOf(OFFICER);

        group.changeAvailability();
    }

    public void joinGroup(Long groupId, LocalDate date) {
        final String lockName = "group:" + groupId;
        final RLock lock = redissonClient.getLock(lockName);
        try {
            if (!lock.tryLock(1, 5, TimeUnit.SECONDS)) {
                return;
            }
            groupServiceTrans.processJoinGroup(groupId, date);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    public List<ClubMemberInfoResponse> getGroupMemberList(Long groupId) {
        Group group = groupRepository.getById(groupId);
        List<GroupMember> groupMembers = groupMemberRepository.getAllByGroup(group);

        return groupMembers.stream()
                .map(GroupMember::getClubMember)
                .map(ClubMemberInfoResponse::from)
                .toList();
    }
}
