package woohakdong.server.api.service.group;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static woohakdong.server.common.exception.CustomErrorInfo.GROUP_MEMBER_LIMIT_EXCEEDED;
import static woohakdong.server.common.exception.CustomErrorInfo.GROUP_NOT_FOUND;
import static woohakdong.server.domain.clubmember.ClubMemberRole.PRESIDENT;
import static woohakdong.server.domain.group.GroupType.EVENT;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import woohakdong.server.api.controller.group.dto.GroupCreateRequest;
import woohakdong.server.api.controller.group.dto.GroupIdResponse;
import woohakdong.server.api.controller.group.dto.GroupInfoResponse;
import woohakdong.server.api.controller.group.dto.GroupUpdateRequest;
import woohakdong.server.api.service.SecurityContextSetUp;
import woohakdong.server.common.exception.CustomException;
import woohakdong.server.common.util.date.DateUtil;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.club.ClubRepository;
import woohakdong.server.domain.clubmember.ClubMember;
import woohakdong.server.domain.clubmember.ClubMemberRepository;
import woohakdong.server.domain.clubmember.ClubMemberRole;
import woohakdong.server.domain.group.Group;
import woohakdong.server.domain.group.GroupRepository;
import woohakdong.server.domain.group.GroupType;
import woohakdong.server.domain.member.Member;

class GroupServiceTest extends SecurityContextSetUp {

    @Autowired
    private GroupService groupService;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private ClubMemberRepository clubMemberRepository;

    @Autowired
    private DateUtil dateUtil;

    @BeforeEach
    void setUp() {
        club = createClub();
        member = createExampleMember();
        createClubMember(club, member, PRESIDENT);
    }

    private Club club;
    private Member member;

    @DisplayName("새로운 EVENT 그룹을 등록할 수 있다.")
    @Test
    void registerNewEventGroup() {
        // Given
        GroupCreateRequest request = createNewGroupRequest("동아리 MT", 0);
        LocalDate date = LocalDate.of(2024, 11, 19);

        // When
        GroupIdResponse groupId = groupService.createEventGroup(club.getClubId(), request, date);

        // Then
        Group group = groupRepository.getById(groupId.groupId());
        String expectedJoinLink = "https://www.woohakdong.com/clubs/%s/groups/%d".formatted(club.getClubEnglishName(),
                group.getGroupId());

        assertThat(group)
                .extracting("groupName", "groupAmount", "groupType", "groupJoinLink")
                .contains("동아리 MT", 0, EVENT, expectedJoinLink);
    }

    @DisplayName("그룹 1개의 정보를 불러올 수 있다.")
    @Test
    void getGroupInfo() {
        // Given
        Group group = createNewGroup("동아리 MT", 0, EVENT, true, 0, 999);
        LocalDate date = LocalDate.of(2024, 11, 19);

        // When
        GroupInfoResponse response = groupService.findOneGroup(group.getGroupId(), date);

        // Then
        assertThat(response)
                .extracting("groupName", "groupAmount")
                .contains("동아리 MT", 0);
    }

    @DisplayName("동아리의 모든 EVENT 그룹을 불러올 수 있다.")
    @Test
    void findAllEventGroupOfClub() {
        // Given
        createNewGroup("동아리 MT", 0, EVENT, true, 0, 999);
        createNewGroup("스프링 스터디", 10, EVENT, true, 0, 999);
        createNewGroup("자바 스터디", 5, EVENT, true, 0, 999);

        // When
        List<GroupInfoResponse> responses = groupService.findAllEventGroupOfClub(club.getClubId());

        // Then
        assertThat(responses)
                .extracting("groupName", "groupAmount")
                .containsExactlyInAnyOrder(
                        tuple("동아리 MT", 0),
                        tuple("스프링 스터디", 10),
                        tuple("자바 스터디", 5)
                );
    }

    @DisplayName("그룹 정보를 수정할 수 있다.")
    @Test
    void updateGroupInfo() {
        // Given
        Group group = createNewGroup("동아리 MT", 0, EVENT, true, 0, 999);
        GroupUpdateRequest request = createGroupUpdateRequest("스프링 스터디", false, 999);
        LocalDate date = LocalDate.of(2024, 11, 19);

        // When
        GroupIdResponse response = groupService.updateGroupInfo(group.getGroupId(), request, date);

        // Then
        Group updatedGroup = groupRepository.getById(response.groupId());
        assertThat(updatedGroup)
                .extracting("groupName", "groupIsActivated")
                .contains("스프링 스터디", false);
    }

    @DisplayName("그룹 정보를 수정할 때, 인원 제한을 초과하면 예외를 던진다.")
    @Test
    void updateGroupInfoThrowExceedException() {
        // Given
        Group group = createNewGroup("동아리 MT", 0, EVENT, true, 5, 10);
        GroupUpdateRequest request = createGroupUpdateRequest("동아리 MT", true, 4);
        LocalDate date = LocalDate.of(2024, 11, 19);

        // When & Then
        assertThatThrownBy(() -> groupService.updateGroupInfo(group.getGroupId(), request, date))
                .isInstanceOf(CustomException.class)
                .hasMessage(GROUP_MEMBER_LIMIT_EXCEEDED.getMessage());
    }

    @DisplayName("그룹을 삭제할 수 있다.")
    @Test
    void deleteGroup() {
        // Given
        Group group = createNewGroup("동아리 MT", 0, EVENT, true, 0, 999);
        LocalDate date = LocalDate.of(2024, 11, 19);

        // When
        groupService.deleteGroup(group.getGroupId(), date);

        // Then
        assertThatThrownBy(() -> groupRepository.getById(group.getGroupId()))
                .isInstanceOf(CustomException.class)
                .hasMessage(GROUP_NOT_FOUND.getMessage());
    }

    @DisplayName("그룹의 사용 가능 여부를 변경할 수 있다.")
    @Test
    void changeAvailabilityOfGroup() {
        // Given
        Group group = createNewGroup("동아리 MT", 0, EVENT, true, 0, 999);
        LocalDate date = LocalDate.of(2024, 11, 19);

        // When
        groupService.changeAvailabilityOfGroup(group.getGroupId(), date);

        // Then
        Group updatedGroup = groupRepository.getById(group.getGroupId());
        assertThat(updatedGroup.getGroupIsActivated()).isFalse();
    }


    private Group createNewGroup(String groupName, int groupAmount, GroupType groupType, boolean activated,
                                 int memberCount, int memberLimit) {
        Group group = Group.builder()
                .groupName(groupName)
                .groupAmount(groupAmount)
                .groupType(groupType)
                .club(club)
                .groupIsActivated(activated)
                .groupMemberLimit(memberLimit)
                .groupMemberCount(memberCount)
                .build();
        return groupRepository.save(group);
    }


    private Club createClub() {
        Club club = Club.builder()
                .clubName("테스트 동아리")
                .clubEnglishName("testClub")
                .clubGroupChatLink("https://club-group-chat-link.com")
                .clubExpirationDate(LocalDate.of(2024, 11, 19))
                .build();
        return clubRepository.save(club);
    }

    private GroupCreateRequest createNewGroupRequest(String name, int amount) {
        return GroupCreateRequest.builder()
                .groupName(name)
                .groupDescription("신나는 동아리 MT입니다.")
                .groupChatLink("https://test-chat.com")
                .groupChatPassword("test-password")
                .groupAmount(amount)
                .groupMemberLimit(999)
                .build();
    }

    private GroupUpdateRequest createGroupUpdateRequest(String name, boolean isActivated, int memberLimit) {
        return GroupUpdateRequest.builder()
                .groupName(name)
                .groupDescription("스프링을 공부하는 스터디입니다.")
                .groupChatLink("https://spring-study-chat.com")
                .groupChatPassword("spring-password")
                .groupIsActivated(isActivated)
                .groupMemberLimit(memberLimit)
                .build();
    }

    private void createClubMember(Club club, Member member, ClubMemberRole role) {
        ClubMember clubMember = ClubMember.builder()
                .club(club)
                .member(member)
                .clubMemberRole(role)
                .clubMemberAssignedTerm(dateUtil.getAssignedTerm(LocalDate.of(2024, 11, 19)))
                .build();
        clubMemberRepository.save(clubMember);
    }
}