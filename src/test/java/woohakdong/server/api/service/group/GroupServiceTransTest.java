package woohakdong.server.api.service.group;

import static org.assertj.core.api.Assertions.assertThat;
import static woohakdong.server.domain.clubmember.ClubMemberRole.PRESIDENT;
import static woohakdong.server.domain.group.GroupType.EVENT;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import woohakdong.server.SecurityContextSetup;
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

class GroupServiceTransTest extends SecurityContextSetup {

    @Autowired
    private GroupServiceTrans groupServiceTrans;

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

    @DisplayName("그룹에 참가할 수 있다.")
    @Test
    void processJoinGroup() {
        // Given
        Group group = createNewGroup("동아리 MT", 0, EVENT, true, 0, 999);
        LocalDate date = LocalDate.of(2024, 11, 19);

        // When
        groupServiceTrans.processJoinGroup(group.getGroupId(), date);

        // Then
        Group updatedGroup = groupRepository.getById(group.getGroupId());
        assertThat(updatedGroup.getGroupMemberCount()).isEqualTo(1);
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