package woohakdong.server.api.service.club;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static woohakdong.server.common.exception.CustomErrorInfo.CLUB_NAME_DUPLICATION;
import static woohakdong.server.common.exception.CustomErrorInfo.CLUB_NOT_FOUND;
import static woohakdong.server.domain.clubmember.ClubMemberRole.PRESIDENT;
import static woohakdong.server.domain.group.GroupType.JOIN;

import java.time.LocalDate;
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
import woohakdong.server.api.controller.ListWrapperResponse;
import woohakdong.server.api.controller.club.dto.*;
import woohakdong.server.common.exception.CustomException;
import woohakdong.server.common.security.jwt.CustomUserDetails;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.club.ClubRepository;
import woohakdong.server.domain.clubAccount.ClubAccount;
import woohakdong.server.domain.clubAccount.ClubAccountRepository;
import woohakdong.server.domain.clubHistory.ClubHistory;
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

    @Autowired
    private ClubAccountRepository clubAccountRepository;

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
        ClubCreateRequest clubCreateRequest = createClubCreateRequest();

        // When
        ClubCreateResponse clubCreateResponse = clubService.registerClub(clubCreateRequest);

        // Then
        Optional<Club> optionalClub = clubRepository.findById(clubCreateResponse.clubId());
        assertThat(optionalClub).isPresent();
        Club club = optionalClub.get();

        assertThat(club.getClubName()).isEqualTo("두리안");
        assertThat(club.getClubEnglishName()).isEqualTo("Durian");
        assertThat(club.getClubGeneration()).isEqualTo("33");
        assertThat(clubCreateResponse.clubId()).isEqualTo(club.getClubId());
        assertThat(club.getGroups().size()).isEqualTo(1);
        assertThat(club.getGroups().get(0).getGroupAmount()).isEqualTo(10000);
        assertThat(club.getGroups().get(0).getGroupType()).isEqualTo(JOIN);
    }

    @DisplayName("동아리를 등록하면, 등록한 사람이 회장으로 등록된다.")
    @Test
    void registerClubPresidentCheck() {
        // Given
        ClubCreateRequest clubCreateRequest = createClubCreateRequest();

        // When
        ClubCreateResponse clubCreateResponse = clubService.registerClub(clubCreateRequest);

        // Then
        Optional<Club> optionalClub = clubRepository.findById(clubCreateResponse.clubId());
        assertThat(optionalClub).isPresent();
        Club club = optionalClub.get();

        assertThat(club.getClubMembers().size()).isEqualTo(1);
        assertThat(club.getClubMembers().get(0).getClubMemberRole()).isEqualTo(PRESIDENT);
        assertThat(club.getClubMembers().get(0).getMember().getMemberProvideId()).isEqualTo("testProvideId");
    }

    @DisplayName("동아리 회장은 동아리 계좌를 등록할 수 있다.")
    @Test
    void registerClubAccount() {
        // Given
        ClubCreateRequest clubCreateRequest = createClubCreateRequest();
        Long clubId = clubService.registerClub(clubCreateRequest).clubId();
        Club club = clubRepository.findById(clubId).get();

        ClubAccountRegisterRequest clubAccountRegisterRequest = ClubAccountRegisterRequest.builder()
                .clubAccountBankName("국민은행")
                .clubAccountNumber("1234567890")
                .clubAccountPinTechNumber("123456")
                .build();

        // When
        clubService.registerClubAccount(clubId, clubAccountRegisterRequest);

        // Then
        Optional<ClubAccount> clubAccount = clubAccountRepository.findByClub(club);

        assertThat(clubAccount).isPresent();
        assertThat(clubAccount.get().getClubAccountBankName()).isEqualTo("국민은행");
        assertThat(clubAccount.get().getClubAccountNumber()).isEqualTo("1234567890");
    }

    @DisplayName("동아리의 clubName과 clubEnglishName가 모두 중복이 아니라면 유효하다.")
    @Test
    void validateClubWithNames() {
        // Given
        School school = School.builder()
                .schoolDomain("ajou.ac.kr")
                .schoolName("아주대학교")
                .build();
        schoolRepository.save(school);

        Club club = Club.builder()
                .clubName("두리안")
                .clubEnglishName("Durian")
                .school(school)
                .build();
        clubRepository.save(club);

        // When & Then
        clubService.validateClubWithNames("바나나", "Banana");
        clubService.validateClubWithNames("딸기", "Strawberry");
    }

    @DisplayName("동아리의 clubName과 clubEnglishName 중 하나라도 중복이라면 유효하지 않다.")
    @Test
    void validateClubWithNamesWithSameName() {
        // Given
        School school = School.builder()
                .schoolDomain("ajou.ac.kr")
                .schoolName("아주대학교")
                .build();
        schoolRepository.save(school);

        Club club = Club.builder()
                .clubName("두리안")
                .clubEnglishName("Durian")
                .school(school)
                .build();
        clubRepository.save(club);

        // When & Then
        assertThatThrownBy(() -> clubService.validateClubWithNames("두리안", "Banana"))
                .isInstanceOf(CustomException.class)
                .hasMessage(CLUB_NAME_DUPLICATION.getMessage());

        assertThatThrownBy(() -> clubService.validateClubWithNames("Banana", "Durian"))
                .isInstanceOf(CustomException.class)
                .hasMessage(CLUB_NAME_DUPLICATION.getMessage());
    }

    @DisplayName("clubId를 이용하여 동아리 정보를 조회할 수 있다.")
    @Test
    void findClubInfo() {
        // Given
        School school = School.builder()
                .schoolDomain("ajou.ac.kr")
                .schoolName("아주대학교")
                .build();
        schoolRepository.save(school);

        Club club = Club.builder()
                .clubName("두리안")
                .clubEnglishName("Durian")
                .clubGeneration("33")
                .clubDues(10000)
                .school(school)
                .build();
        Club saved = clubRepository.save(club);

        Long clubId = saved.getClubId();

        // When
        ClubInfoResponse response = clubService.findClubInfo(clubId);

        // Then
        assertThat(response).isNotNull()
                .extracting("clubName", "clubEnglishName", "clubGeneration", "clubDues")
                .containsExactly("두리안", "Durian", "33", 10000);
    }

    @DisplayName("존재하지 않는 clubId로 동아리 정보를 조회하면 예외가 발생한다.")
    @Test
    void findClubInfoWithInvalidClubId() {
        // Given
        School school = School.builder()
                .schoolDomain("ajou.ac.kr")
                .schoolName("아주대학교")
                .build();
        schoolRepository.save(school);

        Club club = Club.builder()
                .clubName("두리안")
                .clubEnglishName("Durian")
                .school(school)
                .build();
        Club saved = clubRepository.save(club);

        Long invalidClubId = saved.getClubId() + 1;

        // When & Then
        assertThatThrownBy(() -> clubService.findClubInfo(invalidClubId))
                .isInstanceOf(CustomException.class)
                .hasMessage(CLUB_NOT_FOUND.getMessage());
    }

    @DisplayName("동아리의 clubEnglishName으로 동아리 정보를 조회할 수 있다.")
    @Test
    void findClubInfoWithEnglishName() {
        // Given
        School school = School.builder()
                .schoolDomain("ajou.ac.kr")
                .schoolName("아주대학교")
                .build();
        schoolRepository.save(school);

        Club club = Club.builder()
                .clubName("두리안")
                .clubEnglishName("Durian")
                .school(school)
                .build();
        Club saved = clubRepository.save(club);

        // When
        ClubInfoResponse response = clubService.findClubInfoWithEnglishName("Durian");

        // Then
        assertThat(response).isNotNull()
                .extracting("clubName", "clubEnglishName")
                .containsExactly("두리안", "Durian");
    }

    @DisplayName("동아리의 우학동 서비스 사용 분기를 리스트로 확인할 수 있다.")
    @Test
    void checkClubHistory() {
        // Given
        School school = School.builder()
                .schoolDomain("ajou.ac.kr")
                .schoolName("아주대학교")
                .build();
        schoolRepository.save(school);

        Club club = Club.builder()
                .clubName("두리안")
                .clubEnglishName("Durian")
                .school(school)
                .build();

        ClubHistory clubHistory1 = ClubHistory.builder()
                .club(club)
                .clubHistoryUsageDate(LocalDate.of(2023, 1, 1))
                .build();

        ClubHistory clubHistory2 = ClubHistory.builder()
                .club(club)
                .clubHistoryUsageDate(LocalDate.of(2024, 1, 1))
                .build();

        club.addClubHistory(clubHistory1);
        club.addClubHistory(clubHistory2);

        Club saved = clubRepository.save(club);

        // When
        ListWrapperResponse<ClubHistoryTermResponse> result = clubService.getClubHistory(saved.getClubId());

        // Then
        assertThat(result).isNotNull();
        assertThat(result.result()).hasSize(2);
        assertThat(result.result().get(0).clubHistoryUsageDate()).isEqualTo(LocalDate.of(2023, 1, 1));
        assertThat(result.result().get(1).clubHistoryUsageDate()).isEqualTo(LocalDate.of(2024, 1, 1));
    }


    private ClubCreateRequest createClubCreateRequest() {
        return ClubCreateRequest.builder()
                .clubName("두리안")
                .clubEnglishName("Durian")
                .clubDues(10000)
                .clubGeneration("33")
                .build();
    }
}