package woohakdong.server.domain.gathering;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;
import static woohakdong.server.domain.gathering.GatheringType.EVENT;
import static woohakdong.server.domain.gathering.GatheringType.JOIN;

import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
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
class GatheringRepositoryTest {

    @Autowired
    private GatheringRepository gatheringRepository;

    @Autowired
    private ClubRepository clubRepository;

    @DisplayName("동아리의 가입용 그룹을 조회할 수 있다.")
    @Test
    void findByClubAndGatheringType() {
        // Given
        Club club = Club.builder()
                .clubName("테스트 동아리")
                .clubEnglishName("test_club")
                .build();
        Club savedClub = clubRepository.save(club);

        Gathering gathering1 = Gathering.builder()
                .club(club)
                .gatheringName("가입용 모임")
                .gatheringType(JOIN)
                .gatheringLink("https://test.com")
                .build();

        Gathering gathering2 = Gathering.builder()
                .club(club)
                .gatheringType(EVENT)
                .gatheringName("이벤트용 모임")
                .gatheringLink("https://test.com")
                .build();
        gatheringRepository.saveAll(List.of(gathering1, gathering2));

        // When
        Optional<Gathering> optionalGathering = gatheringRepository.findByClubAndGatheringType(savedClub, JOIN);

        // Then
        assertThat(optionalGathering).isPresent();

        Gathering foundGathering = optionalGathering.get();
        assertThat(foundGathering.getGatheringType()).isEqualTo(JOIN);
        assertThat(foundGathering.getGatheringName()).isEqualTo("가입용 모임");
    }

    @DisplayName("동아리의 이벤트용 그룹을 조회할 수 있다.")
    @Test
    void findAllByClubAndGatheringType() {
        // Given
        Club club = Club.builder()
                .clubName("테스트 동아리")
                .clubEnglishName("test_club")
                .build();
        Club savedClub = clubRepository.save(club);

        Gathering gathering1 = Gathering.builder()
                .club(club)
                .gatheringName("가입용 모임")
                .gatheringType(JOIN)
                .gatheringLink("https://test.com")
                .build();

        Gathering gathering2 = Gathering.builder()
                .club(club)
                .gatheringType(EVENT)
                .gatheringName("MT")
                .gatheringAmount(15000)
                .gatheringLink("https://test.com")
                .build();

        Gathering gathering3 = Gathering.builder()
                .club(club)
                .gatheringType(EVENT)
                .gatheringName("스터디")
                .gatheringAmount(5000)
                .gatheringLink("https://test.com")
                .build();

        gatheringRepository.saveAll(List.of(gathering1, gathering2, gathering3));
        // When
        List<Gathering> gatherings = gatheringRepository.findAllByClubAndGatheringType(savedClub, EVENT);

        // Then
        assertThat(gatherings).hasSize(2)
                .extracting("gatheringName", "gatheringAmount")
                .containsExactlyInAnyOrder(
                        tuple("MT", 15000),
                        tuple("스터디", 5000)
                );
    }
}