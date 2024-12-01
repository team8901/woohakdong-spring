package woohakdong.server.performance;

import static org.assertj.core.api.Assertions.assertThat;
import static woohakdong.server.config.TestConstants.TEST_PROVIDE_ID;
import static woohakdong.server.domain.clubmember.ClubMemberRole.PRESIDENT;
import static woohakdong.server.domain.group.GroupType.EVENT;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import woohakdong.server.api.service.group.GroupService;
import woohakdong.server.common.util.date.DateUtil;
import woohakdong.server.config.WithMockCustomUser;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.club.ClubRepository;
import woohakdong.server.domain.clubmember.ClubMember;
import woohakdong.server.domain.clubmember.ClubMemberRepository;
import woohakdong.server.domain.clubmember.ClubMemberRole;
import woohakdong.server.domain.group.Group;
import woohakdong.server.domain.group.GroupRepository;
import woohakdong.server.domain.group.GroupType;
import woohakdong.server.domain.groupmember.GroupMember;
import woohakdong.server.domain.groupmember.GroupMemberJpaRepository;
import woohakdong.server.domain.member.Member;
import woohakdong.server.domain.member.MemberRepository;

@ActiveProfiles("test")
@SpringBootTest
@WithMockCustomUser
@Disabled
public class GroupMultiThreadTest {

    @Autowired
    private MemberRepository memberRepository;

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

    @Autowired
    private GroupMemberJpaRepository groupMemberJpaRepository;

    public Member createExampleMember() {
        Member member = Member.builder()
                .memberProvideId(TEST_PROVIDE_ID)
                .memberName("john doe")
                .memberEmail("johnDoe@example.com")
                .build();
        return memberRepository.save(member);
    }

    @BeforeEach
    void setUp() {
        club = createClub();
        member = createExampleMember();
        createClubMember(club, member, PRESIDENT);
        group = createNewGroup("testGroup", 10000, EVENT, true, 0, 7);
    }

    private Club club;
    private Member member;
    private Group group;

    @DisplayName("동시에 그룹에 10명의 인원이 참가하면, 동시성 제어가 실패한다.")
    @Test
    void test1() throws InterruptedException {
        // Given
        LocalDate date = LocalDate.of(2024, 11, 19);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failureCount = new AtomicInteger();

        // When
        int numberOfThreads = 100; // 동시 실행할 쓰레드 수
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads); // 쓰레드 동기화용

        for (int i = 0; i < numberOfThreads; i++) {
            executorService.execute(() -> {
                try {
                    groupService.joinGroup(group.getGroupId(), date); // 테스트 대상 메서드 호출
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    System.out.println("예외 발생: " + e.getMessage());
                } finally {
                    latch.countDown(); // 작업 완료시 latch 감소
                }
            });
        }

        latch.await(); // 모든 쓰레드가 작업을 완료할 때까지 대기
        executorService.shutdown();
        System.out.println("성공한 쓰레드 수: " + successCount.get());
        System.out.println("실패한 쓰레드 수: " + failureCount.get());

        // Then
        List<GroupMember> all = groupMemberJpaRepository.findAll();
        System.out.println("저장된 그룹 멤버 수: " + all.size());
        assertThat(all.size()).isEqualTo(successCount.get()); // 성공한 쓰레드 수와 저장된 그룹 멤버 수 일치 확인
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
