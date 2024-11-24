package woohakdong.server.api.service.admin.overall;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static woohakdong.server.domain.clubmember.ClubMemberRole.MEMBER;

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
import woohakdong.server.api.controller.admin.overall.dto.ClubPaymentResponse;
import woohakdong.server.api.controller.admin.overall.dto.CountResponse;
import woohakdong.server.api.controller.admin.overall.dto.SchoolListResponse;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.club.ClubRepository;
import woohakdong.server.domain.clubHistory.ClubHistory;
import woohakdong.server.domain.clubHistory.ClubHistoryRepository;
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
class AdminOverallServiceTest {

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private AdminOverallService adminOverallService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ClubMemberRepository clubMemberRepository;

    @Autowired
    private ClubHistoryRepository clubHistoryRepository;

    @BeforeEach
    void setup() {
        // 초기 데이터 삽입 예시
        School school1 = createSchool("Test School 1", "school1.edu");
        School school2 = createSchool("Test School 2", "school2.edu");
        Club club1 = createClub(school1, "Club A", "English Club A");
        Club club2 = createClub(school2, "Club B", "English Club B");
        Member member1 = createMember(school1, "testProvideId1", "박상준", "sangjun@ajou.ac.kr");
        Member member2 = createMember(school1, "testProvideId2", "정의엽", "uiyeop@ajou.ac.kr");
        createClubMember(club1, member1, MEMBER, LocalDate.now());
        createClubMember(club1, member2, MEMBER, LocalDate.now());
        createClubMember(club2, member1, MEMBER, LocalDate.now());
        createClubHistory(club1, LocalDate.of(2024, 7,1));
        createClubHistory(club2, LocalDate.of(2024, 7,1));
    }

    @Test
    @DisplayName("전체 학교 수 반환 테스트")
    void getTotalSchoolCount() {
        CountResponse response = adminOverallService.getTotalSchoolCount(null);
        assertThat(response.count()).isEqualTo(2); // 초기 데이터 기준 2개 학교 존재
    }

    @Test
    @DisplayName("전체 학교 리스트 반환 테스트")
    void getAllSchools() {
        // when
        List<SchoolListResponse> response = adminOverallService.getAllSchools(null);

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
        CountResponse response = adminOverallService.getTotalClubCount(null);

        // then
        assertThat(response.count()).isEqualTo(2); // 초기 데이터 기준 2개 동아리 존재
    }

    @Test
    @DisplayName("전체 동아리 리스트 반환 테스트")
    void getAllClubs() {
        // when
        List<ClubListResponse> response = adminOverallService.getAllClubs(null);

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
        CountResponse response = adminOverallService.getTotalMemberCount(null);

        // then
        assertThat(response.count()).isEqualTo(3);
    }

    @Test
    @DisplayName("분기별 학교 수 반환 테스트")
    void getTermSchoolCount() {
        CountResponse response = adminOverallService.getTotalSchoolCount(LocalDate.of(2024,7,1));
        assertThat(response.count()).isEqualTo(2); // 초기 데이터 기준 2개 학교 존재
    }

    @Test
    @DisplayName("분기별 학교 리스트 반환 테스트")
    void getTermSchools() {
        // when
        List<SchoolListResponse> response = adminOverallService.getAllSchools(LocalDate.of(2024,7,1));

        // then
        assertThat(response).hasSize(2)
                .extracting("schoolName", "schoolDomain")
                .containsExactlyInAnyOrder(
                        tuple("Test School 1", "school1.edu"),
                        tuple("Test School 2", "school2.edu")
                );
    }

    @Test
    @DisplayName("분기별 동아리 수 반환 테스트")
    void getTermClubCount() {
        // when
        CountResponse response = adminOverallService.getTotalClubCount(LocalDate.of(2024,7,1));

        // then
        assertThat(response.count()).isEqualTo(2);
    }

    @Test
    @DisplayName("분기별 동아리 리스트 반환 테스트")
    void getTermClubs() {
        // when
        List<ClubListResponse> response = adminOverallService.getAllClubs(LocalDate.of(2024,7,1));

        // then
        assertThat(response).hasSize(2)
                .extracting("clubName", "clubEnglishName", "schoolName")
                .containsExactlyInAnyOrder(
                        tuple("Club A", "English Club A", "Test School 1"),
                        tuple("Club B", "English Club B", "Test School 2")
                );
    }


    @Test
    @DisplayName("분기별 멤버 수 반환 테스트")
    void getTermMemberCount() {
        // when
        CountResponse response = adminOverallService.getTotalMemberCount(LocalDate.of(2024,7,1));

        // then
        assertThat(response.count()).isEqualTo(3);
    }

    @Test
    @DisplayName("결제 금액 - assignedTerm이 null일 때 전체 회원 기준")
    void getClubPaymentWithoutAssignedTerm() {
        // When
        ClubPaymentResponse response = adminOverallService.getClubPaymentByTerm(null);

        // Then
        assertThat(response.clubPayment()).isEqualTo(30000 + (3 * 500));
    }

    @Test
    @DisplayName("결제 금액 - 특정 assignedTerm 기준")
    void getClubPaymentWithAssignedTerm() {
        // Given
        LocalDate assignedTerm = LocalDate.of(2024, 7, 1);

        // When
        ClubPaymentResponse response = adminOverallService.getClubPaymentByTerm(assignedTerm);

        // Then
        assertThat(response.clubPayment()).isEqualTo(30000 + (3 * 500));
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

    private ClubHistory createClubHistory(Club club, LocalDate date) {
        ClubHistory clubHistory = ClubHistory.create(club, date);
        return clubHistoryRepository.save(clubHistory);
    }
}