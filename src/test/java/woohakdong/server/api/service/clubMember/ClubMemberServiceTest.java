package woohakdong.server.api.service.clubMember;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import woohakdong.server.api.controller.clubMember.dto.ClubMemberInfoResponse;
import woohakdong.server.common.exception.CustomErrorInfo;
import woohakdong.server.common.exception.CustomException;
import woohakdong.server.common.security.jwt.CustomUserDetails;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.club.ClubRepository;
import woohakdong.server.domain.clubmember.ClubMember;
import woohakdong.server.domain.clubmember.ClubMemberRepository;
import woohakdong.server.domain.clubmember.ClubMemberRole;
import woohakdong.server.domain.member.Member;
import woohakdong.server.domain.member.MemberRepository;
import woohakdong.server.domain.school.School;
import woohakdong.server.domain.school.SchoolRepository;


@ActiveProfiles("test")
@SpringBootTest
@Transactional
class ClubMemberServiceTest {

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

    @BeforeEach
    void setUp() {
        school = createSchool("ajou.ac.kr");
        club = createClub(school);
        member = createMember(school, "testProvideId1", "박상준", "sangjun@ajou.ac.kr");
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

        LocalDate now = LocalDate.of(2024, 11, 2);
        createClubMember(club, member1, MEMBER, getAssignedTerm(now));
        createClubMember(club, member2, OFFICER, getAssignedTerm(now));

        // when
        List<ClubMemberInfoResponse> responses = clubMemberService.getMembers(club.getClubId());

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

        createClubMember(club, member1, MEMBER, LocalDate.of(2024, 1, 1));
        createClubMember(club, member2, OFFICER, LocalDate.of(2024, 2, 1));
        createClubMember(club, member3, MEMBER, LocalDate.of(2024, 6, 30));
        createClubMember(club, member3, OFFICER, LocalDate.of(2024, 7, 1));

        // when
        List<ClubMemberInfoResponse> responses = clubMemberService.getFilteredMembers(club.getClubId(),
                null, LocalDate.of(2024, 1, 1));

        // then
        assertThat(responses).hasSize(3)
                .extracting("memberName", "clubMemberRole")
                .containsExactlyInAnyOrder(
                        tuple("박상준", MEMBER),
                        tuple("준상박", OFFICER),
                        tuple("김상박", MEMBER)
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

        LocalDate now = LocalDate.of(2024, 1, 1);

        // When
        List<ClubMemberInfoResponse> responses = clubMemberService.getFilteredMembers(club.getClubId(), "가나", now);

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
        Member member = setUpMemberSession("박상준");
        createClubMember(club, member, MEMBER, LocalDate.now());

        // When
        ClubMemberInfoResponse response = clubMemberService.getMyInfo(club.getClubId());

        // Then
        assertThat(response)
                .extracting("memberName", "clubMemberRole")
                .containsExactly("박상준", MEMBER);
    }

    @DisplayName("동아리 회장이라면, 멤버의 역할을 변경할 수 있다.")
    @Test
    void changeClubMemberRole() {
        // Given
        Member member = setUpMemberSession("박상준");
        createClubMember(club, member, PRESIDENT, LocalDate.now());

        Member member2 = createMember(school, "testProvideId2", "김상준", "kimsang@ajou.ac.kr");
        ClubMember clubMember2 = createClubMember(club, member2, MEMBER, LocalDate.now());

        // When
        clubMemberService.changeClubMemberRole(club.getClubId(), clubMember2.getClubMemberId(), OFFICER);

        // Then
        ClubMember savedClubMember = clubMemberRepository.getByClubAndMember(club, member2);
        assertThat(savedClubMember)
                .extracting("member.memberName", "clubMemberRole")
                .containsExactly("김상준", OFFICER);
    }

    @DisplayName("동아리 회장이 아니라면, 멤버의 역할을 변경할 수 없다.")
    @Test
    void changeClubMemberRoleWithOutPRESIDENTRole() {
        // Given
        Member member = setUpMemberSession("박상준");
        createClubMember(club, member, VICEPRESIDENT, LocalDate.now());

        Member member2 = createMember(school, "testProvideId2", "김상준", "kimsang@ajou.ac.kr");
        ClubMember clubMember2 = createClubMember(club, member2, MEMBER, LocalDate.now());

        // When & Then
        assertThatThrownBy(
                () -> clubMemberService.changeClubMemberRole(club.getClubId(), clubMember2.getClubMemberId(), OFFICER))
                .isInstanceOf(CustomException.class)
                .hasMessage(CustomErrorInfo.CLUB_MEMBER_ROLE_NOT_ALLOWED.getMessage());
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

    private ClubMember createClubMember(Club club, Member member, ClubMemberRole memberRole, LocalDate assignedTerm) {
        ClubMember clubMember = ClubMember.builder()
                .clubMemberAssignedTerm(getAssignedTerm(assignedTerm))
                .club(club)
                .member(member)
                .clubMemberRole(memberRole)
                .build();
        return clubMemberRepository.save(clubMember);
    }

    private LocalDate getAssignedTerm(LocalDate date) {
        int year = date.getYear();
        int semester = date.getMonthValue() <= 6 ? 1 : 7; // 1: 1학기, 7: 2학기
        return LocalDate.of(year, semester, 1);
    }

    private Member setUpMemberSession(String name) {
        String provideId = "testProvideId";
        Member member = Member.builder()
                .memberProvideId(provideId)
                .memberName(name)
                .memberEmail("john.doe@example.com")
                .build();
        memberRepository.save(member);

        String role = "USER_ROLE";
        CustomUserDetails customUserDetails = new CustomUserDetails(provideId, role);
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        return member;
    }
}