package woohakdong.server.api.service.notification;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willDoNothing;
import static woohakdong.server.config.TestConstants.TEST_PROVIDE_ID;
import static woohakdong.server.domain.clubmember.ClubMemberRole.OFFICER;
import static woohakdong.server.domain.clubmember.ClubMemberRole.PRESIDENT;
import static woohakdong.server.domain.member.MemberGender.MAN;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import woohakdong.server.SecurityContextSetup;
import woohakdong.server.api.service.email.EmailClientImpl;
import woohakdong.server.common.util.date.DateUtil;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.club.ClubRepository;
import woohakdong.server.domain.clubmember.ClubMember;
import woohakdong.server.domain.clubmember.ClubMemberRepository;
import woohakdong.server.domain.clubmember.ClubMemberRole;
import woohakdong.server.domain.member.Member;
import woohakdong.server.domain.member.MemberRepository;
import woohakdong.server.domain.schedule.Schedule;
import woohakdong.server.domain.schedule.ScheduleRepository;

class NotificationServiceTest extends SecurityContextSetup {

    @Autowired
    private NotificationService notificationService;

    @MockBean
    private EmailClientImpl emailClientImpl;

    @TestConfiguration
    static class TestConfig {

        @Bean
        public TaskExecutor taskExecutor() {
            return new SyncTaskExecutor();
        }
    }

    @Autowired
    private ClubMemberRepository clubMemberRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private DateUtil dateUtil;

    @BeforeEach
    void setUp() {
        member = createMember(TEST_PROVIDE_ID, "박상준", "sangjun@example.com");
        Member member2 = createMember("testProvideId2", "김우학", "woohak@example.com");
        Member member3 = createMember("testProvideId3", "최햇반", "hatban@example.com");
        club = createClub("두잇");
        createClubMember(member, club, PRESIDENT, date);
        createClubMember(member2, club, OFFICER, date);
        createClubMember(member3, club, OFFICER, date);
    }

    private Member member;
    private Club club;
    private LocalDate date = LocalDate.of(2024, 11, 2);


    @DisplayName("동아리 정보를 포함한 이메일을 동아리원들에게 전송할 수 있다.")
    @Test
    void sendNotificationWithClubInfoUpdate() {
        // Given
        Long clubId = club.getClubId();

        willDoNothing()
                .given(emailClientImpl)
                .sendEmailForUpdateClubInfo(any(), any(), any(), any(), any(), any());

        // When & Then
        notificationService.sendNotificationWithClubInfoUpdate(clubId, date);
    }

    @DisplayName("일정 정보를 포함한 이메일을 동아리원들에게 전송할 수 있다.")
    @Test
    void sendNotificationWithSchedule() {
        // Given
        Schedule schedule = createSchedule("일정 제목", "일정 내용", date.atStartOfDay());

        Long clubId = club.getClubId();
        Long scheduleId = schedule.getScheduleId();

        willDoNothing()
                .given(emailClientImpl)
                .sendEmailForSchedule(any(), any(), any(), any(), any(), any());

        // When & Then
        notificationService.sendNotificationWithSchedule(clubId, scheduleId, date);
    }

    private Member createMember(String provideId, String name, String email) {
        Member member = Member.builder()
                .memberProvideId(provideId)
                .memberName(name)
                .memberEmail(email)
                .memberPhoneNumber("01012345678")
                .memberMajor("Computer Science")
                .memberStudentNumber("20210001")
                .memberGender(MAN)
                .build();
        return memberRepository.save(member);
    }

    private Club createClub(String name) {
        Club club = Club.builder()
                .clubName(name)
                .clubEnglishName("testClub")
                .clubDescription("testDescription")
                .clubGroupChatLink("https://club-group-chat-link.com")
                .clubExpirationDate(LocalDate.of(2024, 11, 19))
                .build();
        return clubRepository.save(club);
    }

    private ClubMember createClubMember(Member member, Club club, ClubMemberRole role, LocalDate date) {
        LocalDate assignedTerm = dateUtil.getAssignedTerm(date);
        ClubMember clubMember = ClubMember.builder()
                .member(member)
                .club(club)
                .clubMemberAssignedTerm(assignedTerm)
                .clubMemberRole(role)
                .clubJoinedDate(date)
                .build();
        return clubMemberRepository.save(clubMember);
    }

    private Schedule createSchedule(String title, String content, LocalDateTime date) {
        Schedule schedule = Schedule.builder()
                .scheduleTitle(title)
                .scheduleContent(content)
                .scheduleDateTime(date)
                .club(club)
                .build();
        return scheduleRepository.save(schedule);
    }
}