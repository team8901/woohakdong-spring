package woohakdong.server.domain.clubmember;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static woohakdong.server.domain.clubmember.ClubMemberRole.MEMBER;
import static woohakdong.server.domain.clubmember.ClubMemberRole.OFFICER;
import static woohakdong.server.domain.clubmember.ClubMemberRole.PRESIDENT;

import java.time.LocalDate;
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
import woohakdong.server.common.util.date.DateUtil;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.club.ClubRepository;
import woohakdong.server.domain.member.Member;
import woohakdong.server.domain.member.MemberRepository;
import woohakdong.server.domain.school.School;
import woohakdong.server.domain.school.SchoolRepository;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class ClubMemberRepositoryTest {

    @Autowired
    private ClubMemberRepository clubMemberRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private DateUtil dateUtil;

    @BeforeEach
    void setUp() {
        school = createSchool("ajou.ac.kr");
        club = createClub(school);
        member = createMember(school, "testProvideId", "박상준", "sangjun@ajou.ac.kr");
    }

    private School school;
    private Club club;
    private Member member;

    @DisplayName("동아리에 가입한 회원에 대해 역할이 부여되었는지 확인한다.")
    @ParameterizedTest(name = "{index} =>, role={0}, expected={1}")
    @CsvSource({
            "MEMBER, false",
            "PRESIDENT, true",
            "OFFICER, false",
            "VICEPRESIDENT, false",
            "SECRETARY, false",
    })
    void existsByClubAndMemberAndClubMemberRole(ClubMemberRole role, boolean expected) {
        // Given
        createClubMember(club, member, PRESIDENT, LocalDate.of(2024, 11, 19));

        // When
        boolean result = clubMemberRepository.existsByClubAndMemberAndClubMemberRole(club, member, role);

        // Then
        assertThat(result).isEqualTo(expected);
    }

    @DisplayName("동아리에 가입하지 않은 clubMemberId로 조회할 경우 예외를 발생시킨다.")
    @Test
    void getByIdThrowException() {
        // When & Then
        assertThatThrownBy(() -> clubMemberRepository.getById(1L))
                .isInstanceOf(CustomException.class)
                .hasMessage(CustomErrorInfo.CLUB_MEMBER_NOT_FOUND.getMessage());
    }

    @DisplayName("존재하지 않는 동아리로 clubMember를 조회할 경우 예외를 발생시킨다.")
    @Test
    void getByClubAndMember() {
        // When & Then
        assertThatThrownBy(() -> clubMemberRepository.getByClubAndMember(null, member))
                .isInstanceOf(CustomException.class)
                .hasMessage(CustomErrorInfo.CLUB_MEMBER_NOT_FOUND.getMessage());
    }

    @DisplayName("특정 분기에 가입한 동아리 회원의 수를 조회한다.")
    @Test
    void countByClubAndAssignedTerm() {
        // Given
        Member member1 = createMember(school, "testProvideId1", "김준상", "kimjun@ajou.ac.kr");
        Member member2 = createMember(school, "testProvideId2", "이준상", "eejun@ajou.ac.kr");
        Member member3 = createMember(school, "testProvideId3", "최염수", "yeomsu@ajou.ac.kr");
        Member member4 = createMember(school, "testProvideId4", "옥수수", "corn@ajou.ac.kr");
        Member member5 = createMember(school, "testProvideId5", "박상준", "sangjunpark@ajou.ac.kr");

        createClubMember(club, member1, PRESIDENT, LocalDate.of(2024, 4, 1));
        createClubMember(club, member2, OFFICER, LocalDate.of(2024, 7, 1));
        createClubMember(club, member3, MEMBER, LocalDate.of(2024, 9, 1));
        createClubMember(club, member4, MEMBER, LocalDate.of(2024, 12, 31));
        createClubMember(club, member5, MEMBER, LocalDate.of(2025, 1, 1));

        // When
        LocalDate assignedTerm = dateUtil.getAssignedTerm(LocalDate.of(2024, 11, 19));
        Integer count = clubMemberRepository.countByClubAndAssignedTerm(club, assignedTerm);

        // Then
        assertThat(count).isEqualTo(3);
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
                .clubExpirationDate(LocalDate.of(2024, 11, 19))
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
                .clubMemberAssignedTerm(dateUtil.getAssignedTerm(assignedTerm))
                .club(club)
                .member(member)
                .clubMemberRole(memberRole)
                .build();
        return clubMemberRepository.save(clubMember);
    }
}