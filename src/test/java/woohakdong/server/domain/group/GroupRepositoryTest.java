package woohakdong.server.domain.group;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static woohakdong.server.domain.group.GroupType.EVENT;
import static woohakdong.server.domain.group.GroupType.JOIN;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
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

    @BeforeEach
    void setUp() {
        club = createClub();
    }

    private Club club;

    @DisplayName("동아리의 가입용 그룹을 조회할 수 있다.")
    @Test
    void findByClubAndGroupTypeAndGroupIsAvailable() {
        // Given
        createGroup("가입용 모임", JOIN, 10000);
        createGroup("이벤트용 모임", EVENT, 10000);

        // When
        Group group = groupRepository.getByClubAndGroupType(club, JOIN);

        assertThat(group)
                .extracting("groupType", "groupName")
                .containsExactly(JOIN, "가입용 모임");
    }

    @DisplayName("동아리의 이벤트용 그룹을 조회할 수 있다.")
    @Test
    void findAllByClubAndGroupType() {
        // Given
        createGroup("가입용 모임", JOIN, 10000);
        createGroup("MT", EVENT, 15000);
        createGroup("스터디", EVENT, 5000);

        // When
        List<Group> groups = groupRepository.getAllByClubAndGroupType(club, EVENT);

        // Then
        assertThat(groups).hasSize(2)
                .extracting("groupName", "groupAmount")
                .containsExactlyInAnyOrder(
                        tuple("MT", 15000),
                        tuple("스터디", 5000)
                );
    }

    private Group createGroup(String name, GroupType type, Integer amount) {
        Group group = Group.builder()
                .club(club)
                .groupName(name)
                .groupType(type)
                .groupJoinLink("https://test.com")
                .groupAmount(amount)
                .groupIsActivated(true)
                .build();
        return groupRepository.save(group);
    }

    private Club createClub() {
        Club club = Club.builder()
                .clubName("테스트 동아리")
                .clubEnglishName("Test Club")
                .clubGroupChatLink("https://club-group-chat-link.com")
                .clubExpirationDate(LocalDate.of(2024, 11, 19))
                .build();
        return clubRepository.save(club);
    }
}