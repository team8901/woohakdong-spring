package woohakdong.server.api.service.schedule;

import static java.time.LocalDateTime.of;
import static org.assertj.core.api.Assertions.assertThat;
import static woohakdong.server.domain.clubmember.ClubMemberRole.OFFICER;
import static woohakdong.server.domain.member.MemberGender.MAN;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import woohakdong.server.api.controller.schedule.dto.ScheduleCreateRequest;
import woohakdong.server.api.controller.schedule.dto.ScheduleIdResponse;
import woohakdong.server.api.controller.schedule.dto.ScheduleInfoResponse;
import woohakdong.server.common.security.jwt.CustomUserDetails;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.club.ClubRepository;
import woohakdong.server.domain.clubmember.ClubMember;
import woohakdong.server.domain.clubmember.ClubMemberRepository;
import woohakdong.server.domain.member.Member;
import woohakdong.server.domain.member.MemberRepository;
import woohakdong.server.domain.schedule.Schedule;
import woohakdong.server.domain.schedule.ScheduleRepository;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class ScheduleServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private ClubMemberRepository clubMemberRepository;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @BeforeEach
    void setUp() {
        String provideId = "testProvideId";
        CustomUserDetails userDetails = new CustomUserDetails(provideId, "USER_ROLE");
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        member = createMember(provideId, "박상준");
        club = createClub("두잇");
        clubMember = createClubMember(member, club);
    }

    private Member member;
    private Club club;
    private ClubMember clubMember;

    @DisplayName("동아리 일정을 생성한다.")
    @Test
    void createScheduleTest() {
        // Given
        ScheduleCreateRequest request = createScheduleCreateRequest("스프링 스터디", "3회차", of(2024, 11, 4, 20, 0));

        // When
        ScheduleIdResponse response = scheduleService.createSchedule(club.getClubId(), request);

        // Then
        Schedule schedule = scheduleRepository.getById(response.scheduleId());
        assertThat(schedule)
                .extracting("scheduleTitle", "scheduleContent", "scheduleDateTime")
                .containsExactly("스프링 스터디", "3회차", of(2024, 11, 4, 20, 0));
    }

    @DisplayName("동아리 일정 1개를 조회한다.")
    @Test
    void getSchedule() {
        // Given
        Long scheduleId = createSchedule("리액트 스터디", "4회차", of(2024, 11, 4, 20, 0)).getScheduleId();
        Long clubId = club.getClubId();

        // When
        ScheduleInfoResponse response = scheduleService.getSchedule(clubId, scheduleId);

        // Then
        assertThat(response)
                .extracting("scheduleTitle", "scheduleContent", "scheduleDateTime")
                .containsExactly("리액트 스터디", "4회차", of(2024, 11, 4, 20, 0));
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

    private ScheduleCreateRequest createScheduleCreateRequest(String title, String content, LocalDateTime dateTime) {
        return ScheduleCreateRequest.builder()
                .scheduleTitle(title)
                .scheduleContent(content)
                .scheduleColor("#00ff0088")
                .scheduleDateTime(dateTime)
                .build();
    }

    private Member createMember(String provideId, String name) {
        Member member = Member.builder()
                .memberProvideId(provideId)
                .memberName(name)
                .memberEmail("john.doe@example.com")
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
                .clubGroupChatLink("https://club-group-chat-link.com")
                .build();
        return clubRepository.save(club);
    }

    private ClubMember createClubMember(Member member, Club club) {
        ClubMember clubMember = ClubMember.builder()
                .member(member)
                .club(club)
                .clubMemberAssignedTerm(LocalDate.of(2024, 7, 1))
                .clubMemberRole(OFFICER)
                .clubJoinedDate(LocalDate.of(2024, 8, 1))
                .build();
        return clubMemberRepository.save(clubMember);
    }
}