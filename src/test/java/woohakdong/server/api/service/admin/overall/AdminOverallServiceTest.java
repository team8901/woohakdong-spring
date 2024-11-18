package woohakdong.server.api.service.admin.overall;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import java.time.LocalDate;
import java.util.List;
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
import woohakdong.server.domain.school.School;
import woohakdong.server.domain.school.SchoolRepository;


@ActiveProfiles("test")
@SpringBootTest
@Transactional
class AdminOverallServiceTest {

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private AdminOverallService adminOverallService;

    @BeforeEach
    void setup() {
        // 초기 데이터 삽입 예시
        School school1 = createSchool("Test School 1", "school1.edu");
        School school2 = createSchool("Test School 2", "school2.edu");
        createClub(school1, "Club A", "English Club A");
        createClub(school2, "Club B", "English Club B");
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
        assertThat(response).hasSize(2)
                .extracting("schoolName", "schoolDomain")
                .containsExactlyInAnyOrder(
                        tuple("Test School 1", "school1.edu"),
                        tuple("Test School 2", "school2.edu")
                );
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
        assertThat(response).hasSize(2)
                .extracting("clubName", "clubEnglishName", "schoolName")
                .containsExactlyInAnyOrder(
                        tuple("Club A", "English Club A", "Test School 1"),
                        tuple("Club B", "English Club B", "Test School 2")
                );
    }


    @Test
    @DisplayName("전체 멤버 수 반환 테스트")
    void getTotalMemberCount() {
        // when
        CountResponse response = adminOverallService.getTotalMemberCount();

        // then
        assertThat(response.count()).isEqualTo(0); // 초기 데이터에 멤버 없음
    }

    private Club createClub(School school1, String name, String englishName) {
        Club club = Club.builder()
                .clubName(name)
                .clubEnglishName(englishName)
                .clubGroupChatLink("qwer")
                .clubExpirationDate(LocalDate.of(2024, 11, 19))
                .school(school1)
                .build();
        return clubRepository.save(club);
    }

    private School createSchool(String name, String domain) {
        School school = School.builder()
                .schoolName(name)
                .schoolDomain(domain)
                .build();
        return schoolRepository.save(school);
    }
}