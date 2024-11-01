package woohakdong.server.domain.club;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
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

    @DisplayName("동아리 이름 또는 영어 이름으로 동아리가 존재하는지 확인한다.")
    @Test
    void existsByClubNameOrClubEnglishName() {
        // Given
        School school = School.builder()
                .schoolDomain("ajou.ac.kr")
                .schoolName("아주대학교")
                .build();
        schoolRepository.save(school);

        Club club1 = Club.builder()
                .clubName("테스트동아리")
                .clubEnglishName("testClub")
                .clubGroupChatLink("https://club-group-chat-link.com")
                .school(school)
                .build();

        Club club2 = Club.builder()
                .clubName("테스트동아리2")
                .clubEnglishName("testClub2")
                .clubGroupChatLink("https://club-group-chat-link.com")
                .school(school)
                .build();
        clubRepository.save(club1);
        clubRepository.save(club2);

        // When
        Boolean result1 = clubRepository.existsByClubNameOrClubEnglishName(club1.getClubName(), "nonexistent");
        Boolean result2 = clubRepository.existsByClubNameOrClubEnglishName("존재하지않는이름", club2.getClubEnglishName());
        Boolean result3 = clubRepository.existsByClubNameOrClubEnglishName(club1.getClubName(), club2.getClubEnglishName());
        Boolean result4 = clubRepository.existsByClubNameOrClubEnglishName("존재하지않는이름", "nonexistent");

        // Then
        assertThat(result1).isTrue();
        assertThat(result2).isTrue();
        assertThat(result3).isTrue();
        assertThat(result4).isFalse();
    }
}