package woohakdong.server.api.service.club;

import static org.assertj.core.api.Assertions.assertThat;
import static woohakdong.server.domain.gathering.GatheringType.JOIN;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import woohakdong.server.api.controller.club.dto.ClubCreateRequest;
import woohakdong.server.api.controller.club.dto.ClubCreateResponse;
import woohakdong.server.common.security.jwt.CustomUserDetails;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.club.ClubRepository;
import woohakdong.server.domain.member.Member;
import woohakdong.server.domain.member.MemberRepository;
import woohakdong.server.domain.school.School;
import woohakdong.server.domain.school.SchoolRepository;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class ClubServiceTest {

    @Autowired
    private ClubService clubService;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private SchoolRepository schoolRepository;

    @BeforeEach
    void setUp() {
        School school = School.builder()
                .schoolDomain("ajou.ac.kr")
                .schoolName("아주대학교")
                .build();
        schoolRepository.save(school);

        Member member = Member.builder()
                .memberProvideId("testProvideId")
                .memberName("John Doe")
                .memberRole("USER_ROLE")
                .memberEmail("")
                .school(school)
                .build();
        memberRepository.save(member);

        // SecurityContext에 CustomUserDetails 설정
        String provideId = "testProvideId";
        String role = "USER_ROLE";
        CustomUserDetails customUserDetails = new CustomUserDetails(provideId, role);
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    @DisplayName("동아리를 등록하면, JOIN 타입의 모임이 생성된다.")
    @Test
    void registerClub() {
        // Given
        ClubCreateRequest clubCreateRequest = ClubCreateRequest.builder()
                .clubName("두리안")
                .clubEnglishName("Durian")
                .clubDues(10000)
                .clubGeneration("33")
                .build();

        // When
        ClubCreateResponse clubCreateResponse = clubService.registerClub(clubCreateRequest);

        // Then
        Optional<Club> optionalClub = clubRepository.findById(clubCreateResponse.clubId());
        assertThat(optionalClub).isPresent();
        Club club = optionalClub.get();

        assertThat(club.getClubName()).isEqualTo("두리안");
        assertThat(club.getClubEnglishName()).isEqualTo("Durian");
        assertThat(clubCreateResponse.clubId()).isEqualTo(club.getClubId());
        assertThat(club.getGatherings().size()).isEqualTo(1);
        assertThat(club.getGatherings().get(0).getGatheringAmount()).isEqualTo(10000);
        assertThat(club.getGatherings().get(0).getGatheringType()).isEqualTo(JOIN);
    }
}