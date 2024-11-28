package woohakdong.server.api.service.clubMember;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static woohakdong.server.config.TestConstants.TEST_PROVIDE_ID;
import static woohakdong.server.domain.clubmember.ClubMemberRole.MEMBER;
import static woohakdong.server.domain.clubmember.ClubMemberRole.OFFICER;
import static woohakdong.server.domain.clubmember.ClubMemberRole.PRESIDENT;
import static woohakdong.server.domain.clubmember.ClubMemberRole.VICEPRESIDENT;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import woohakdong.server.api.controller.clubMember.dto.ClubMemberInfoResponse;
import woohakdong.server.api.service.SecurityContextSetUp;
import woohakdong.server.common.exception.CustomErrorInfo;
import woohakdong.server.common.exception.CustomException;
import woohakdong.server.common.util.date.DateUtil;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.club.ClubRepository;
import woohakdong.server.domain.clubAccount.ClubAccount;
import woohakdong.server.domain.clubAccount.ClubAccountRepository;
import woohakdong.server.domain.clubmember.ClubMember;
import woohakdong.server.domain.clubmember.ClubMemberRepository;
import woohakdong.server.domain.clubmember.ClubMemberRole;
import woohakdong.server.domain.member.Member;
import woohakdong.server.domain.member.MemberRepository;
import woohakdong.server.domain.school.School;
import woohakdong.server.domain.school.SchoolRepository;


class ClubMemberServiceTest extends SecurityContextSetUp {

    @Autowired
    private ClubMemberService clubMemberService;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private ClubMemberRepository clubMemberRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private ClubAccountRepository clubAccountRepository;

    @Autowired
    private DateUtil dateUtil;

    @BeforeEach
    void setUp() {
        school = createSchool("ajou.ac.kr");
        club = createClub(school);
        member = createMember(school, TEST_PROVIDE_ID, "박상준", "sangjun@ajou.ac.kr");
    }

    private Member member;
    private School school;
    private Club club;

    @DisplayName("동아리 멤버 리스트를 확인할 수 있다.")
    @Test
    void getMembers() {
        // given
        Member member1 = createMember(school, "testProvideId2", "박상준", "sangjun@ajou.ac.kr");
        Member member2 = createMember(school, "testProvideId3", "준상박", "junsang@ajou.ac.kr");

        LocalDate date = LocalDate.of(2024, 11, 2);
        createClubMember(club, member1, MEMBER, date);
        createClubMember(club, member2, OFFICER, date);

        // when
        List<ClubMemberInfoResponse> responses = clubMemberService.getMembers(club.getClubId(), date);

        // then
        assertThat(responses).hasSize(2)
                .extracting("memberName", "clubMemberRole")
                .containsExactlyInAnyOrder(
                        tuple("박상준", MEMBER),
                        tuple("준상박", OFFICER)
                );
    }

    @DisplayName("분기별 동아리 멤버 리스트를 확인할 수 있다.")
    @Test
    void getFilteredMembersWithTerm() {
        // given
        Member member1 = createMember(school, "testProvideId2", "박상준", "sangjun@ajou.ac.kr");
        Member member2 = createMember(school, "testProvideId3", "준상박", "junsang@ajou.ac.kr");
        Member member3 = createMember(school, "testProvideId4", "김상박", "kimsang@ajou.ac.kr");

        createClubMember(club, member1, MEMBER, LocalDate.of(2024, 2, 28));
        createClubMember(club, member2, OFFICER, LocalDate.of(2024, 3, 1));
        createClubMember(club, member3, OFFICER, LocalDate.of(2024, 7, 1));

        // when
        List<ClubMemberInfoResponse> responses = clubMemberService.getFilteredMembers(club.getClubId(), null,
                LocalDate.of(2024, 4, 1));

        // then
        assertThat(responses).hasSize(2)
                .extracting("memberName", "clubMemberRole")
                .containsExactlyInAnyOrder(
                        tuple("준상박", OFFICER),
                        tuple("김상박", OFFICER)
                );
    }

    @DisplayName("이름으로 이번 분기의 동아리 멤버 리스트를 확인할 수 있다.")
    @Test
    void getFilteredMembersWithName() {
        // Given
        Member member1 = createMember(school, "testProvideId2", "박가나", "pgana@ajou.ac.kr");
        Member member2 = createMember(school, "testProvideId3", "가나다", "nada@ajou.ac.k나");
        Member member3 = createMember(school, "testProvideId4", "김가나", "kgana@ajou.ac.kr");
        Member member4 = createMember(school, "testProvideId5", "가김나", "kimna@ajou.ac.kr");

        createClubMember(club, member1, MEMBER, LocalDate.of(2024, 1, 1));
        createClubMember(club, member2, OFFICER, LocalDate.of(2024, 2, 1));
        createClubMember(club, member3, MEMBER, LocalDate.of(2024, 7, 1));
        createClubMember(club, member4, OFFICER, LocalDate.of(2024, 1, 1));

        LocalDate date = LocalDate.of(2024, 1, 1);

        // When
        List<ClubMemberInfoResponse> responses = clubMemberService.getFilteredMembers(club.getClubId(), "가나", date);

        // Then
        assertThat(responses).hasSize(2)
                .extracting("memberName", "clubMemberRole")
                .containsExactlyInAnyOrder(
                        tuple("박가나", MEMBER),
                        tuple("가나다", OFFICER)
                );
    }

    @DisplayName("동아리 내 나의 정보를 확인할 수 있다.")
    @Test
    void getMyInfo() {
        // Given
        LocalDate date = LocalDate.of(2024, 11, 19);
        createClubMember(club, member, MEMBER, date);

        // When
        ClubMemberInfoResponse response = clubMemberService.getMyInfo(club.getClubId(), date);

        // Then
        assertThat(response)
                .extracting("memberName", "clubMemberRole")
                .containsExactly("박상준", MEMBER);
    }

    @DisplayName("동아리 회장이라면, 멤버의 역할을 변경할 수 있다.")
    @Test
    void changeClubMemberRole() {
        // Given
        LocalDate date = LocalDate.of(2024, 11, 19);
        LocalDate assignedTerm = dateUtil.getAssignedTerm(date);
        createClubMember(club, member, PRESIDENT, date);

        Member member2 = createMember(school, "testProvideId2", "김상준", "kimsang@ajou.ac.kr");
        ClubMember clubMember2 = createClubMember(club, member2, MEMBER, date);

        Long clubId = club.getClubId();
        Long clubMemberId = clubMember2.getClubMemberId();

        // When
        clubMemberService.changeClubMemberRole(clubId, clubMemberId, OFFICER, date);

        // Then
        ClubMember saved = clubMemberRepository.getByClubAndMemberAndAssignedTerm(club, member2, assignedTerm);
        assertThat(saved)
                .extracting("member.memberName", "clubMemberRole")
                .containsExactly("김상준", OFFICER);
    }

    @DisplayName("동아리 회장이 아니라면, 멤버의 역할을 변경할 수 없다.")
    @Test
    void changeClubMemberRoleWithOutPRESIDENTRole() {
        // Given
        LocalDate date = LocalDate.of(2024, 11, 19);
        createClubMember(club, member, VICEPRESIDENT, date);

        Member member2 = createMember(school, "testProvideId2", "김상준", "kimsang@ajou.ac.kr");
        ClubMember clubMember2 = createClubMember(club, member2, MEMBER, date);

        Long clubId = club.getClubId();
        Long clubMemberId = clubMember2.getClubMemberId();

        // When & Then
        assertThatThrownBy(
                () -> clubMemberService.changeClubMemberRole(clubId, clubMemberId, OFFICER, date))
                .isInstanceOf(CustomException.class)
                .hasMessage(CustomErrorInfo.CLUB_MEMBER_ROLE_NOT_ALLOWED.getMessage());
    }

    @DisplayName("동아리 회장을 위임하면, 본인은 임원진으로 변경된다.")
    @Test
    void passOnThePresidency() {
        // Given
        LocalDate date = LocalDate.of(2024, 11, 19);
        Member member2 = createMember(school, "testProvideId3", "준상박", "junsang@ajou.ac.kr");
        createClubMember(club, member, PRESIDENT, date);
        createClubMember(club, member2, VICEPRESIDENT, date);
        createClubAccount(club);

        // When
        clubMemberService.passOnThePresidency(club.getClubId(), member2.getMemberId(), date);

        // Then
        List<ClubMember> clubMemberList = clubMemberRepository.getAll();
        assertThat(clubMemberList).hasSize(2)
                .extracting("member.memberName", "clubMemberRole")
                .containsExactlyInAnyOrder(
                        tuple("박상준", OFFICER),
                        tuple("준상박", PRESIDENT)
                );
    }

    @DisplayName("동아리 회장을 위임하면, 해당 동아리의 연동 계좌가 삭제된다.")
    @Test
    void passOnThePresidencyThenClubAccountRemoved() {
        // Given
        LocalDate date = LocalDate.of(2024, 11, 19);
        Member member2 = createMember(school, "testProvideId3", "준상박", "junsang@ajou.ac.kr");
        createClubMember(club, member, PRESIDENT, date);
        createClubMember(club, member2, VICEPRESIDENT, date);
        ClubAccount clubAccount = createClubAccount(club);

        // When
        clubMemberService.passOnThePresidency(club.getClubId(), member2.getMemberId(), date);

        // Then
        assertThatThrownBy(
                () -> clubAccountRepository.getByClub(club))
                .isInstanceOf(CustomException.class)
                .hasMessage(CustomErrorInfo.CLUB_ACCOUNT_NOT_FOUND.getMessage());
    }

    private School createSchool(String domain) {
        School school = School.builder()
                .schoolDomain(domain)
                .schoolName("아주대학교")
                .build();
        return schoolRepository.save(school);
    }

    private Club createClub(School school) {
        Club club = Club.builder()
                .clubName("테스트 동아리")
                .clubEnglishName("testClub")
                .clubGroupChatLink("https://club-group-chat-link.com")
                .clubExpirationDate(LocalDate.of(2025, 3, 2))
                .school(school)
                .build();
        return clubRepository.save(club);
    }

    private Member createMember(School school, String provideId, String name, String email) {
        Member member = Member.builder()
                .memberProvideId(provideId)
                .memberName(name)
                .memberEmail(email)
                .school(school)
                .build();
        return memberRepository.save(member);
    }

    private ClubMember createClubMember(Club club, Member member, ClubMemberRole memberRole, LocalDate date) {
        LocalDate assignedTerm = dateUtil.getAssignedTerm(date);
        ClubMember clubMember = ClubMember.builder()
                .clubMemberAssignedTerm(assignedTerm)
                .club(club)
                .member(member)
                .clubMemberRole(memberRole)
                .build();
        return clubMemberRepository.save(clubMember);
    }

    private ClubAccount createClubAccount(Club club) {
        ClubAccount clubAccount = ClubAccount.builder()
                .club(club)
                .clubAccountBankName("아주은행")
                .clubAccountNumber("1234567890")
                .clubAccountPinTechNumber("PIN-123456")
                .clubAccountBankCode("123")
                .clubAccountBalance(0L)
                .build();
        return clubAccountRepository.save(clubAccount);
    }

}