package woohakdong.server.api.service.club;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static woohakdong.server.common.exception.CustomErrorInfo.CLUB_EXPIRED;
import static woohakdong.server.common.exception.CustomErrorInfo.CLUB_NAME_DUPLICATION;
import static woohakdong.server.common.exception.CustomErrorInfo.CLUB_NOT_FOUND;
import static woohakdong.server.config.TestConstants.TEST_PROVIDE_ID;
import static woohakdong.server.domain.clubmember.ClubMemberRole.OFFICER;
import static woohakdong.server.domain.clubmember.ClubMemberRole.PRESIDENT;
import static woohakdong.server.domain.group.GroupType.CLUB_PAYMENT;
import static woohakdong.server.domain.group.GroupType.JOIN;
import static woohakdong.server.domain.member.MemberGender.MAN;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import woohakdong.server.api.controller.club.dto.ClubAccountRegisterRequest;
import woohakdong.server.api.controller.club.dto.ClubAccountResponse;
import woohakdong.server.api.controller.club.dto.ClubCreateRequest;
import woohakdong.server.api.controller.club.dto.ClubHistoryTermResponse;
import woohakdong.server.api.controller.club.dto.ClubIdResponse;
import woohakdong.server.api.controller.club.dto.ClubInfoResponse;
import woohakdong.server.api.controller.club.dto.ClubSummaryResponse;
import woohakdong.server.api.controller.club.dto.ClubUpdateRequest;
import woohakdong.server.api.controller.group.dto.GroupSummaryResponse;
import woohakdong.server.api.service.SecurityContextSetUp;
import woohakdong.server.common.exception.CustomException;
import woohakdong.server.common.util.date.DateUtil;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.club.ClubRepository;
import woohakdong.server.domain.clubAccount.ClubAccount;
import woohakdong.server.domain.clubAccount.ClubAccountRepository;
import woohakdong.server.domain.clubHistory.ClubHistory;
import woohakdong.server.domain.clubmember.ClubMember;
import woohakdong.server.domain.clubmember.ClubMemberRepository;
import woohakdong.server.domain.clubmember.ClubMemberRole;
import woohakdong.server.domain.group.Group;
import woohakdong.server.domain.group.GroupRepository;
import woohakdong.server.domain.group.GroupType;
import woohakdong.server.domain.member.Member;
import woohakdong.server.domain.member.MemberRepository;
import woohakdong.server.domain.school.School;
import woohakdong.server.domain.school.SchoolRepository;

class ClubServiceTest extends SecurityContextSetUp {

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

    @Autowired
    private DateUtil dateUtil;

    @BeforeEach
    void setUp() {
        member = createMember(TEST_PROVIDE_ID, "박상준", "sangjun@ajou.ac.kr");
        school = createSchool();
    }

    private Member member;
    private School school;

    @DisplayName("동아리를 등록하면, JOIN 타입의 모임이 생성된다.")
    @Test
    void registerClub() {
        // Given
        ClubCreateRequest clubCreateRequest = createClubCreateRequest();

        // When
        clubService.registerClub(clubCreateRequest, LocalDate.of(2024, 3, 1));

        // Then
        List<Group> groups = groupRepository.getAll();
        assertThat(groups).hasSize(1)
                .extracting("groupName", "groupType", "groupJoinLink")
                .contains(
                        tuple(clubCreateRequest.clubName(), JOIN, "https://www.woohakdong.com/clubs/Durian")
                );
    }

    @DisplayName("동아리를 등록하면, 등록한 사람이 회장으로 등록된다.")
    @Test
    void registerClubPresidentCheck() {
        // Given
        ClubCreateRequest clubCreateRequest = createClubCreateRequest();

        // When
        ClubIdResponse clubIdResponse = clubService.registerClub(clubCreateRequest, LocalDate.of(2024, 3, 1));

        // Then
        List<ClubMember> clubMembers = clubMemberRepository.getAll();
        assertThat(clubMembers).hasSize(1)
                .extracting("clubMemberRole", "club.clubId")
                .contains(
                        tuple(PRESIDENT, clubIdResponse.clubId())
                );
    }

    @DisplayName("동아리 회장은 동아리 계좌를 등록할 수 있다.")
    @Test
    void registerClubAccount() {
        // Given
        Club club = createClub(school, "두리안", "Durian", LocalDate.of(2024, 11, 19));
        setClubMember(club, LocalDate.now(), PRESIDENT, member);
        LocalDate now = LocalDate.of(2024, 11, 19);

        ClubAccountRegisterRequest request = createClubAccountRegisterRequest("국민은행", "1234567890");

        // When
        clubService.registerClubAccount(club.getClubId(), request, now);

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
        Club club = createClub(school, "두리안", "Durian", LocalDate.of(2024, 11, 19));

        // When & Then
        clubService.validateClubWithNames("바나나", "Banana");
        clubService.validateClubWithNames("딸기", "Strawberry");
    }

    @DisplayName("동아리의 clubName과 clubEnglishName 중 하나라도 중복이라면 유효하지 않다.")
    @Test
    void validateClubWithNamesWithSameName() {
        // Given
        Club club = createClub(school, "두리안", "Durian", LocalDate.of(2024, 3, 1));

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
        Club club = createClub(school, "두리안", "Durian", LocalDate.of(2024, 3, 1));

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
        Club club = createClub(school, "두리안", "Durian", LocalDate.of(2024, 3, 1));
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
        Club club = createClub(school, "두리안", "Durian", LocalDate.of(2024, 3, 1));

        // When
        ClubSummaryResponse response = clubService.findClubInfoWithEnglishName("Durian");

        // Then
        assertThat(response).isNotNull()
                .extracting("clubName", "clubEnglishName")
                .containsExactly("두리안", "Durian");
    }

    @DisplayName("본인이 가입한 동아리 리스트를 조회할 수 있다.")
    @Test
    void getJoinedClubInfos() {
        // Given
        Club club1 = createClub(school, "두리안", "Durian", LocalDate.of(2024, 3, 1));
        Club club2 = createClub(school, "우학동", "WooHakDong", LocalDate.of(2024, 3, 1));
        setClubMember(club1, LocalDate.of(2024, 3, 1), PRESIDENT, member);
        setClubMember(club1, LocalDate.of(2024, 11, 19), OFFICER, member);
        setClubMember(club2, LocalDate.of(2024, 3, 1), PRESIDENT, member);

        // When
        List<ClubInfoResponse> responses = clubService.getJoinedClubInfos();

        // Then
        assertThat(responses).hasSize(2)
                .extracting("clubName", "clubEnglishName")
                .containsExactly(
                        tuple("두리안", "Durian"),
                        tuple("우학동", "WooHakDong")
                );
    }

    @DisplayName("동아리의 우학동 서비스 사용 분기를 리스트로 확인할 수 있다.")
    @Test
    void checkClubHistory() {
        // Given
        Club club = createClub(school, "두리안", "Durian", LocalDate.of(2024, 11, 19));
        createClubHistory(club, 2023, 1);
        createClubHistory(club, 2024, 1);

        // When
        List<ClubHistoryTermResponse> clubHistory = clubService.getClubHistory(club.getClubId());

        // Then
        assertThat(clubHistory).hasSize(2)
                .extracting("clubHistoryUsageDate")
                .containsExactly(LocalDate.of(2023, 1, 1), LocalDate.of(2024, 1, 1));
    }

    @DisplayName("동아리 회장은 정보를 수정할 수 있다.")
    @Test
    void update() {
        // Given
        Club club = createClub(school, "두리안", "Durian", LocalDate.of(2024, 11, 19));
        setClubMember(club, LocalDate.now(), PRESIDENT, member);
        createGroupForClub(club, JOIN, club.getClubDues(), club.getClubName());

        ClubUpdateRequest request = createClubUpdateRequest();

        // When
        clubService.updateClubInfo(club.getClubId(), request);

        // Then
        Club updatedClub = clubRepository.getById(club.getClubId());
        assertThat(updatedClub).extracting("clubImage", "clubDescription", "clubGroupChatLink")
                .containsExactly(request.clubImage(), request.clubDescription(), request.clubGroupChatLink());
    }


    @DisplayName("동아리 회장이 동아리 정보를 수정하면 JOIN 타입의 모임도 수정된다.")
    @Test
    void updateWithGroup() {
        // Given
        Club club = createClub(school, "두리안", "Durian", LocalDate.of(2024, 11, 19));
        setClubMember(club, LocalDate.now(), PRESIDENT, member);
        Group group = createGroupForClub(club, JOIN, club.getClubDues(), club.getClubName());

        ClubUpdateRequest request = createClubUpdateRequest();

        // When
        clubService.updateClubInfo(club.getClubId(), request);

        // Then
        assertThat(group)
                .extracting("groupAmount", "groupChatLink", "groupChatPassword")
                .containsExactly(request.clubDues(), request.clubGroupChatLink(),
                        request.clubGroupChatPassword());
    }

    @DisplayName("동아리의 계좌 정보를 조회할 수 있다")
    @Test
    void getClubAccount() {
        // Given
        Club club = createClub(school, "두리안", "Durian", LocalDate.of(2024, 11, 19));
        ClubAccount clubAccount = createClubAccount(club, "국민은행", "1234567890");

        // When
        ClubAccountResponse response = clubService.getClubAccount(club.getClubId());

        // Then
        assertThat(response).isNotNull()
                .extracting("clubAccountBankName", "clubAccountNumber")
                .containsExactly("국민은행", "1234567890");
    }

    @DisplayName("서비스 사용 기간이 만료되었는지 확인할 수 있다.")
    @Test
    void checkClubExpired() {
        // Given
        Club club = createClub(school, "두리안", "Durian", LocalDate.of(2024, 7, 1));
        LocalDate now = LocalDate.of(2024, 7, 2);

        // When & Then
        assertThatThrownBy(() -> clubService.checkClubExpired(club.getClubId(), now))
                .isInstanceOf(CustomException.class)
                .hasMessage(CLUB_EXPIRED.getMessage());
    }

    @DisplayName("서비스 사용 기간이 만료되었다면, 서비스 사용료 결제를 위한 그룹을 생성한다.")
    @Test
    void checkClubExpiredThenCreateGroupForPayment() {
        // Given
        Club club = createClub(school, "두리안", "Durian", LocalDate.of(2024, 3, 1));
        Member member1 = createMember("testProvideId", "김박수", "baksoo@ajou.ac.kr");
        Member member2 = createMember("testProvideId", "이수박", "watermelon@ajou.ac.kr");
        setClubMember(club, LocalDate.of(2024, 3, 1), PRESIDENT, member);
        setClubMember(club, LocalDate.of(2024, 4, 1), OFFICER, member1);
        setClubMember(club, LocalDate.of(2024, 5, 1), OFFICER, member2);
        LocalDate now = LocalDate.of(2024, 9, 2);

        // When
        assertThatThrownBy(() -> clubService.checkClubExpired(club.getClubId(), now))
                .isInstanceOf(CustomException.class)
                .hasMessage(CLUB_EXPIRED.getMessage());

        // Then
        List<Group> groups = groupRepository.getAll();
        assertThat(groups)
                .extracting("groupType", "groupName", "groupAmount")
                .contains(
                        tuple(CLUB_PAYMENT, "두리안의 우학동 서비스 사용료 결제".substring(0, 15), 31500)
                );
    }

    @DisplayName("동아리 서비스 이용료를 납부하기 위한 그룹 정보를 조회할 수 있다.")
    @Test
    void getGroupPaymentInfo() {
        // Given
        Club club = createClub(school, "두리안", "Durian", LocalDate.of(2024, 3, 1));
        createGroupForClub(club, CLUB_PAYMENT, 35000, club.getClubName() + " 동아리의 우학동 서비스 사용료 결제");

        // When
        GroupSummaryResponse response = clubService.getGroupPaymentInfo(club.getClubId());

        // Then
        assertThat(response).isNotNull()
                .extracting("groupName", "groupAmount")
                .containsExactly(club.getClubName() + " 동아리의 우학동 서비스 사용료 결제", 35000);
    }

    private ClubAccount createClubAccount(Club club, String bankName, String accountNumber) {
        ClubAccount clubAccount = ClubAccount.builder()
                .clubAccountBankName(bankName)
                .clubAccountNumber(accountNumber)
                .clubAccountPinTechNumber("123456")
                .clubAccountBalance(10000000L)
                .clubAccountBankCode("011")
                .club(club)
                .build();
        return clubAccountRepository.save(clubAccount);
    }

    private School createSchool() {
        School school = School.builder()
                .schoolDomain("ajou.ac.kr")
                .schoolName("아주대학교")
                .build();
        schoolRepository.save(school);
        return school;
    }

    private Club createClub(School school, String clubName, String clubEnglishName, LocalDate expirationDate) {
        Club club = Club.builder()
                .clubName(clubName)
                .clubEnglishName(clubEnglishName)
                .clubGeneration("33")
                .clubDues(10000)
                .clubGroupChatLink("https://group-chat.com")
                .school(school)
                .clubExpirationDate(expirationDate)
                .build();
        return clubRepository.save(club);
    }

    private Group createGroupForClub(Club club, GroupType groupType, Integer amount, String name) {
        Group group = Group.builder()
                .club(club)
                .groupType(groupType)
                .groupName(name)
                .groupJoinLink("https://www.wooahakdong.com/clubs/" + club.getClubEnglishName())
                .groupAmount(amount)
                .groupChatLink(club.getClubGroupChatLink())
                .groupChatPassword(club.getClubGroupChatPassword())
                .groupIsActivated(true)
                .groupMemberLimit(999)
                .groupMemberCount(0)
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


    private Member createMember(String provideId, String name, String email) {
        Member member = Member.builder()
                .memberProvideId(provideId)
                .memberName(name)
                .memberEmail(email)
                .memberPhoneNumber("01012345678")
                .memberMajor("Computer Science")
                .memberStudentNumber("20210001")
                .memberGender(MAN)
                .build();
        return memberRepository.save(member);
    }

    private static ClubAccountRegisterRequest createClubAccountRegisterRequest(String bankName, String accountNumber) {
        return ClubAccountRegisterRequest.builder()
                .clubAccountBankName(bankName)
                .clubAccountNumber(accountNumber)
                .clubAccountPinTechNumber("123456")
                .build();
    }

    private ClubMember setClubMember(Club club, LocalDate date, ClubMemberRole clubMemberRole, Member member) {
        ClubMember clubMember = ClubMember.builder()
                .club(club)
                .clubJoinedDate(LocalDate.now())
                .clubMemberAssignedTerm(dateUtil.getAssignedTerm(date))
                .clubMemberRole(clubMemberRole)
                .member(member)
                .build();
        return clubMemberRepository.save(clubMember);
    }

}