package woohakdong.server.api.service.email;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static woohakdong.server.common.exception.CustomErrorInfo.MAIL_SEND_ERROR;
import static woohakdong.server.domain.group.GroupType.JOIN;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import woohakdong.server.common.exception.CustomException;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.group.Group;
import woohakdong.server.domain.member.Member;
import woohakdong.server.domain.school.School;


@SpringBootTest
@ActiveProfiles("test")
@Transactional
class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    @MockBean
    private EmailClientImpl emailClientImpl;

    @TestConfiguration // 테스트에서만 동기적으로 동작하도록 설정
    static class TestConfig {

        @Bean
        public TaskExecutor taskExecutor() {
            return new SyncTaskExecutor();
        }
    }

    @DisplayName("동아리에 새로 가입한 사람에게 이메일을 보낼 수 있다.")
    @Test
    void sendEmailForGroupJoin() {
        // Given
        School school = createSchool();
        Club club = createClub(school, "테스트 동아리", "testClub");
        Group group = createGroup(club, "https://test.com", "test1234");
        Member member = createMember(school, "김우학", "test1234@ajou.ac.kr");

        willDoNothing()
                .given(emailClientImpl)
                .sendEmailForClubJoin(anyString(), anyString(), anyString(), anyString(), anyString(), anyString());

        // When & Then
        emailService.sendEmailForGroupJoin(member.getMemberName(), member.getMemberEmail(), club.getClubName(),
                club.getClubDescription(), group.getGroupChatLink(), group.getGroupChatPassword());
    }

    @DisplayName("메일 전송이 실패하면, 최대 3번까지 재시도한다.")
    @Test
    void sendEmailForGroupJoinRetryTest() {
        // Given
        School school = createSchool();
        Club club = createClub(school, "테스트 동아리", "testClub");
        Group group = createGroup(club, "https://test.com", "test1234");
        Member member = createMember(school, "김우학", "test1234@ajou.ac.kr");

        willThrow(new CustomException(MAIL_SEND_ERROR))
                .given(emailClientImpl)
                .sendEmailForClubJoin(any(), any(), any(), any(), any(), any());

        // When
        emailService.sendEmailForGroupJoin(member.getMemberName(), member.getMemberEmail(), club.getClubName(),
                club.getClubDescription(), group.getGroupChatLink(), group.getGroupChatPassword());

        // Then
        verify(emailClientImpl, times(3))
                .sendEmailForClubJoin(any(), any(), any(), any(), any(), any());
    }

    private static Group createGroup(Club club, String chatLink, String chatPassword) {
        return Group.builder()
                .club(club)
                .groupJoinLink("https://woohakdong.com/" + club.getClubEnglishName())
                .groupChatLink(chatLink)
                .groupChatPassword(chatPassword)
                .groupType(JOIN)
                .build();
    }

    private static Club createClub(School school, String clubName, String club) {
        return Club.builder()
                .clubName(clubName)
                .clubEnglishName(club)
                .clubDescription("testDescription")
                .school(school)
                .build();
    }

    private static Member createMember(School school, String name, String email) {
        return Member.builder()
                .memberName(name)
                .memberEmail(email)
                .memberRole("ROLE_USER")
                .memberProvideId("1234567890")
                .school(school)
                .build();
    }

    private static School createSchool() {
        return School.builder()
                .schoolName("아주대학교")
                .schoolDomain("ajou.ac.kr")
                .build();
    }

}