package woohakdong.server.api.service.club;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static woohakdong.server.common.exception.CustomErrorInfo.CLUB_NAME_DUPLICATION;
import static woohakdong.server.common.exception.CustomErrorInfo.CLUB_NOT_FOUND;
import static woohakdong.server.domain.clubmember.ClubMemberRole.PRESIDENT;
import static woohakdong.server.domain.group.GroupType.JOIN;

import java.time.LocalDate;
import java.util.List;
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
import woohakdong.server.api.controller.club.dto.ClubAccountRegisterRequest;
import woohakdong.server.api.controller.club.dto.ClubCreateRequest;
import woohakdong.server.api.controller.club.dto.ClubCreateResponse;
import woohakdong.server.api.controller.club.dto.ClubHistoryTermResponse;
import woohakdong.server.api.controller.club.dto.ClubInfoResponse;
import woohakdong.server.api.controller.club.dto.ClubUpdateRequest;
import woohakdong.server.common.exception.CustomException;
import woohakdong.server.common.security.jwt.CustomUserDetails;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.club.ClubRepository;
import woohakdong.server.domain.clubAccount.ClubAccount;
import woohakdong.server.domain.clubAccount.ClubAccountRepository;
import woohakdong.server.domain.clubHistory.ClubHistory;
import woohakdong.server.domain.clubmember.ClubMember;
import woohakdong.server.domain.clubmember.ClubMemberRepository;
import woohakdong.server.domain.group.Group;
import woohakdong.server.domain.group.GroupRepository;
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

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private ClubMemberRepository clubMemberRepository;

    @BeforeEach
    void setUp() {
        School school = createSchool();

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
        clubService.registerClub(clubCreateRequest);

        // Then
        List<Group> groups = groupRepository.getAll();
        assertThat(groups).hasSize(1);
        assertThat(groups.get(0)).extracting("groupName", "groupType", "groupJoinLink")
                .containsExactly(clubCreateRequest.clubName(), JOIN, "https://woohakdong.com/clubs/Durian");
    }

    @DisplayName("동아리를 등록하면, 등록한 사람이 회장으로 등록된다.")
    @Test
    void registerClubPresidentCheck() {
        // Given
        ClubCreateRequest clubCreateRequest = createClubCreateRequest();

        // When
        ClubCreateResponse clubCreateResponse = clubService.registerClub(clubCreateRequest);

        // Then
        List<ClubMember> clubMembers = clubMemberRepository.findAll();
        assertThat(clubMembers).hasSize(1);
        assertThat(clubMembers.get(0)).extracting("clubMemberRole", "club.clubId")
                .containsExactly(PRESIDENT, clubCreateResponse.clubId());
    }

    @DisplayName("동아리 회장은 동아리 계좌를 등록할 수 있다.")
    @Test
    void registerClubAccount() {
        // Given
        ClubCreateRequest clubCreateRequest = createClubCreateRequest();
        Long clubId = clubService.registerClub(clubCreateRequest).clubId();
        Club club = clubRepository.getById(clubId);

        ClubAccountRegisterRequest clubAccountRegisterRequest = ClubAccountRegisterRequest.builder()
                .clubAccountBankName("국민은행")
                .clubAccountNumber("1234567890")
                .clubAccountPinTechNumber("123456")
                .build();

        // When
        clubService.registerClubAccount(clubId, clubAccountRegisterRequest);

        // Then
        ClubAccount clubAccount = clubAccountRepository.getByClub(club);
        assertThat(clubAccount)
                .extracting("clubAccountBankName", "clubAccountNumber")
                .containsExactly("국민은행", "1234567890");
    }

    @DisplayName("동아리의 clubName과 clubEnglishName가 모두 중복이 아니라면 유효하다.")
    @Test
    void validateClubWithNames() {
        // Given
        School school = createSchool();
        Club club = createClub(school);

        // When & Then
        clubService.validateClubWithNames("바나나", "Banana");
        clubService.validateClubWithNames("딸기", "Strawberry");
    }

    @DisplayName("동아리의 clubName과 clubEnglishName 중 하나라도 중복이라면 유효하지 않다.")
    @Test
    void validateClubWithNamesWithSameName() {
        // Given
        School school = createSchool();
        Club club = createClub(school);

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
        School school = createSchool();
        Club club = createClub(school);

        // When
        ClubInfoResponse response = clubService.findClubInfo(club.getClubId());

        // Then
        assertThat(response).isNotNull()
                .extracting("clubName", "clubEnglishName", "clubGeneration", "clubDues")
                .containsExactly("두리안", "Durian", "33", 10000);
    }

    @DisplayName("존재하지 않는 clubId로 동아리 정보를 조회하면 예외가 발생한다.")
    @Test
    void findClubInfoWithInvalidClubId() {
        // Given
        School school = createSchool();
        Club club = createClub(school);
        Long invalidClubId = club.getClubId() + 1;

        // When & Then
        assertThatThrownBy(() -> clubService.findClubInfo(invalidClubId))
                .isInstanceOf(CustomException.class)
                .hasMessage(CLUB_NOT_FOUND.getMessage());
    }

    @DisplayName("동아리의 clubEnglishName으로 동아리 정보를 조회할 수 있다.")
    @Test
    void findClubInfoWithEnglishName() {
        // Given
        School school = createSchool();
        Club club = createClub(school);

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
        School school = createSchool();
        Club club = createClub(school);
        ClubHistory clubHistory1 = createClubHistory(club, 2023, 1);
        ClubHistory clubHistory2 = createClubHistory(club, 2024, 1);

        // When
        ListWrapperResponse<ClubHistoryTermResponse> response = clubService.getClubHistory(club.getClubId());

        // Then
        assertThat(response.result()).hasSize(2)
                .extracting("clubHistoryUsageDate")
                .containsExactly(LocalDate.of(2023, 1, 1), LocalDate.of(2024, 1, 1));
    }

    @DisplayName("동아리 정보를 수정할 수 있다.")
    @Test
    void updateClubInfo() {
        // Given
        School school = createSchool();
        Club club = createClub(school);
        Group group = createGroupForClub(club);

        ClubUpdateRequest request = createClubUpdateRequest();

        // When
        clubService.updateClubInfo(club.getClubId(), request);

        // Then
        Club updatedClub = clubRepository.getById(club.getClubId());
        assertThat(updatedClub).extracting("clubImage", "clubDescription", "clubGroupChatLink")
                .containsExactly(request.clubImage(), request.clubDescription(), request.clubGroupChatLink());
    }


    @DisplayName("동아리 정보를 수정하면, 기존 그룹이 disable되고 새로운 그룹이 생성된다.")
    @Test
    void updateClubInfoWithGroup() {
        // Given
        School school = createSchool();
        Club club = createClub(school);
        Group group = createGroupForClub(club);

        ClubUpdateRequest request = createClubUpdateRequest();

        // When
        clubService.updateClubInfo(club.getClubId(), request);

        // Then
        List<Group> groups = groupRepository.getAll();
        assertThat(groups).hasSize(2)
                .extracting("groupAmount", "groupIsAvailable", "groupChatLink", "groupChatPassword")
                .containsExactlyInAnyOrder(
                        tuple(group.getGroupAmount(), false, group.getGroupChatLink(), group.getGroupChatPassword()),
                        tuple(request.clubDues(), true, request.clubGroupChatLink(), request.clubGroupChatPassword()));
    }

    private School createSchool() {
        School school = School.builder()
                .schoolDomain("ajou.ac.kr")
                .schoolName("아주대학교")
                .build();
        schoolRepository.save(school);
        return school;
    }

    private Club createClub(School school) {
        Club club = Club.builder()
                .clubName("두리안")
                .clubEnglishName("Durian")
                .clubGeneration("33")
                .clubDues(10000)
                .clubGroupChatLink("https://group-chat.com")
                .school(school)
                .build();
        return clubRepository.save(club);
    }

    private Group createGroupForClub(Club club) {
        Group group = Group.builder()
                .club(club)
                .groupType(JOIN)
                .groupName(club.getClubName())
                .groupJoinLink("https://wooahakdong.com/clubs/" + club.getClubEnglishName())
                .groupAmount(club.getClubDues())
                .groupChatLink(club.getClubGroupChatLink())
                .groupChatPassword(club.getClubGroupChatPassword())
                .build();
        club.addGroup(group);
        return group;
    }

    private ClubCreateRequest createClubCreateRequest() {
        return ClubCreateRequest.builder()
                .clubName("두리안")
                .clubImage("https://club-image.com")
                .clubDescription("신규 동아리 설명")
                .clubEnglishName("Durian")
                .clubDues(10000)
                .clubGeneration("33")
                .clubGroupChatLink("https://group-chat.com")
                .clubGroupChatPassword("1234")
                .build();
    }

    private static ClubUpdateRequest createClubUpdateRequest() {
        return ClubUpdateRequest.builder()
                .clubDescription("새로운 동아리 설명")
                .clubImage("https://new-club-image.com")
                .clubGroupChatLink("https://new-group-chat.com")
                .clubGroupChatPassword("5678")
                .clubDues(20000)
                .build();
    }

    private ClubHistory createClubHistory(Club club, int year, int month) {
        ClubHistory clubHistory = ClubHistory.builder()
                .club(club)
                .clubHistoryUsageDate(LocalDate.of(year, month, 1))
                .build();
        club.addClubHistory(clubHistory);
        return clubHistory;
    }

}