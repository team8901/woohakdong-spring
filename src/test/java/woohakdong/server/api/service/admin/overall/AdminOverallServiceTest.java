package woohakdong.server.api.service.admin.overall;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import woohakdong.server.api.controller.admin.overall.dto.ClubListResponse;
import woohakdong.server.api.controller.admin.overall.dto.CountResponse;
import woohakdong.server.api.controller.admin.overall.dto.SchoolListResponse;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.club.ClubRepository;
import woohakdong.server.domain.member.MemberRepository;
import woohakdong.server.domain.school.School;
import woohakdong.server.domain.school.SchoolRepository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;


@ActiveProfiles("test")
@SpringBootTest
@Transactional
class AdminOverallServiceTest {

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AdminOverallService adminOverallService;

    @BeforeEach
    void setup() {
        // 초기 데이터 삽입 예시
        School school1 = School.builder()
                .schoolName("Test School 1")
                .schoolDomain("school1.edu")
                .build();
        School school2 = School.builder()
                .schoolName("Test School 2")
                .schoolDomain("school2.edu")
                .build();
        schoolRepository.save(school1);
        schoolRepository.save(school2);

        Club club1 = Club.builder()
                .clubName("Club A")
                .clubEnglishName("English Club A")
                .clubGroupChatLink("qwer")
                .school(school1).build();
        Club club2 = Club.builder()
                .clubName("Club B")
                .clubEnglishName("English Club B")
                .clubGroupChatLink("qwer")
                .school(school2).build();
        clubRepository.save(club1);
        clubRepository.save(club2);
    }

    @Test
    @DisplayName("전체 학교 수 반환 테스트")
    void getTotalSchoolCount() {
        CountResponse response = adminOverallService.getTotalSchoolCount();
        assertThat(response.count()).isEqualTo(2); // 초기 데이터 기준 2개 학교 존재
    }

    @Test
    @DisplayName("전체 학교 리스트 반환 테스트")
    void getAllSchools() {
        // when
        List<SchoolListResponse> response = adminOverallService.getAllSchools();

        // then
        assertThat(response).hasSize(2);
        assertThat(response.get(0))
                .extracting("schoolName", "schoolDomain")
                .containsExactly("Test School 1", "school1.edu");
        assertThat(response.get(1))
                .extracting("schoolName", "schoolDomain")
                .containsExactly("Test School 2", "school2.edu");
    }

    @Test
    @DisplayName("전체 동아리 수 반환 테스트")
    void getTotalClubCount() {
        // when
        CountResponse response = adminOverallService.getTotalClubCount();

        // then
        assertThat(response.count()).isEqualTo(2); // 초기 데이터 기준 2개 동아리 존재
    }

    @Test
    @DisplayName("전체 동아리 리스트 반환 테스트")
    void getAllClubs() {
        // when
        List<ClubListResponse> response = adminOverallService.getAllClubs();

        // then
        assertThat(response).hasSize(2);
        assertThat(response.get(0))
                .extracting("clubName", "clubEnglishName", "schoolName")
                .containsExactly("Club A", "English Club A", "Test School 1");
        assertThat(response.get(1))
                .extracting("clubName", "clubEnglishName", "schoolName")
                .containsExactly("Club B", "English Club B", "Test School 2");
    }

    @Test
    @DisplayName("전체 멤버 수 반환 테스트")
    void getTotalMemberCount() {
        // when
        CountResponse response = adminOverallService.getTotalMemberCount();

        // then
        assertThat(response.count()).isEqualTo(0); // 초기 데이터에 멤버 없음
    }
}