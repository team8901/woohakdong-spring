package woohakdong.server.domain.club;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static woohakdong.server.common.exception.CustomErrorInfo.CLUB_NOT_FOUND;

import java.time.LocalDate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import woohakdong.server.common.exception.CustomErrorInfo;
import woohakdong.server.common.exception.CustomException;
import woohakdong.server.domain.school.School;
import woohakdong.server.domain.school.SchoolRepository;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class ClubRepositoryTest {

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private SchoolRepository schoolRepository;

    @BeforeEach
    void setUp() {
        school = createSchool();
    }

    private School school;

    @DisplayName("동아리 이름 또는 영어 이름으로 동아리가 존재하는지 확인한다.")
    @ParameterizedTest(name = "{index} => clubName={0}, clubEnglishName={1}, expectedResult={2}")
    @CsvSource({
            "테스트동아리, testClub, true",
            "테스트동아리, notExistClub, true",
            "존재하지않는동아리, testClub, true",
            "존재하지않는동아리, 존재하지않는동아리, false"
    })
    void existsByClubNameOrClubEnglishName(String clubName, String clubEnglishName, boolean expectedResult) {
        // Given
        Club club = createClub(school, "테스트동아리", "testClub");

        // When
        Boolean result = clubRepository.existsByClubNameOrClubEnglishName(clubName, clubEnglishName);

        // Then
        assertThat(result).isEqualTo(expectedResult);
    }

    @DisplayName("clubId로 동아리의 존재 여부를 확인한다.")
    @Test
    void validateClubExists() {
        // Given
        Club club = createClub(school, "테스트동아리", "testClub");

        // When & Then
        clubRepository.validateClubExists(club.getClubId());
    }

    @DisplayName("존재하지 않는 clubId로 동아리를 조회하면 CLUB_NOT_FOUND 에러가 발생한다.")
    @Test
    void validateClubExistsThrowEx() {
        // Given
        createClub(school, "테스트동아리", "testClub");

        // When & Then
        assertThatThrownBy(() -> clubRepository.validateClubExists(100L))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(CLUB_NOT_FOUND.getMessage());
    }

    private Club createClub(School school, String name, String clubEnglishName) {
        Club club = Club.builder()
                .clubName(name)
                .clubEnglishName(clubEnglishName)
                .clubGroupChatLink("https://club-group-chat-link.com")
                .clubExpirationDate(LocalDate.of(2024, 11, 19))
                .school(school)
                .build();
        return clubRepository.save(club);
    }

    private School createSchool() {
        School school = School.builder()
                .schoolDomain("ajou.ac.kr")
                .schoolName("아주대학교")
                .build();
        return schoolRepository.save(school);
    }
}