package woohakdong.server.domain.group;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static woohakdong.server.domain.group.GroupType.EVENT;
import static woohakdong.server.domain.group.GroupType.JOIN;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.club.ClubRepository;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class GroupRepositoryTest {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private ClubRepository clubRepository;

    @DisplayName("동아리의 가입용 그룹을 조회할 수 있다.")
    @Test
    void findByClubAndGroupTypeAndGroupIsAvailable() {
        // Given
        Club club = Club.builder()
                .clubName("테스트 동아리")
                .clubEnglishName("test_club")
                .clubGroupChatLink("https://club-group-chat-link.com")
                .build();
        Club savedClub = clubRepository.save(club);

        Group group1 = Group.builder()
                .club(club)
                .groupName("가입용 모임")
                .groupType(JOIN)
                .groupJoinLink("https://test.com")
                .build();

        Group group2 = Group.builder()
                .club(club)
                .groupType(EVENT)
                .groupName("이벤트용 모임")
                .groupJoinLink("https://test.com")
                .build();
        groupRepository.save(group1);
        groupRepository.save(group2);

        // When
        Group group = groupRepository.getByClubAndGroupTypeAndGroupIsAvailable(savedClub, JOIN, true);

        // Then
        assertThat(group.getGroupType()).isEqualTo(JOIN);
        assertThat(group.getGroupName()).isEqualTo("가입용 모임");
    }

    @DisplayName("동아리의 이벤트용 그룹을 조회할 수 있다.")
    @Test
    void findAllByClubAndGroupType() {
        // Given
        Club club = Club.builder()
                .clubName("테스트 동아리")
                .clubEnglishName("test_club")
                .clubGroupChatLink("https://club-group-chat-link.com")
                .build();
        Club savedClub = clubRepository.save(club);

        Group group1 = Group.builder()
                .club(club)
                .groupName("가입용 모임")
                .groupType(JOIN)
                .groupJoinLink("https://test.com")
                .build();

        Group group2 = Group.builder()
                .club(club)
                .groupType(EVENT)
                .groupName("MT")
                .groupAmount(15000)
                .groupJoinLink("https://test.com")
                .build();

        Group group3 = Group.builder()
                .club(club)
                .groupType(EVENT)
                .groupName("스터디")
                .groupAmount(5000)
                .groupJoinLink("https://test.com")
                .build();

        groupRepository.save(group1);
        groupRepository.save(group2);
        groupRepository.save(group3);

        // When
        List<Group> groups = groupRepository.getAllByClubAndGroupType(savedClub, EVENT);

        // Then
        assertThat(groups).hasSize(2)
                .extracting("groupName", "groupAmount")
                .containsExactlyInAnyOrder(
                        tuple("MT", 15000),
                        tuple("스터디", 5000)
                );
    }
}