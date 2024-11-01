package woohakdong.server.domain.clubmember;

import static org.assertj.core.api.Assertions.assertThat;
import static woohakdong.server.domain.clubmember.ClubMemberRole.MEMBER;
import static woohakdong.server.domain.clubmember.ClubMemberRole.OFFICER;
import static woohakdong.server.domain.clubmember.ClubMemberRole.PRESIDENT;

import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
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

    @DisplayName("동아리에 가입한 회원에 대해 역할이 부여되었는지 확인한다.")
    @Test
    void existsByClubAndMemberAndClubMemberRole() {
        // Given
        School school = School.builder()
                .schoolDomain("ajou.ac.kr")
                .schoolName("아주대학교")
                .build();
        schoolRepository.save(school);

        Club club = Club.builder()
                .clubName("테스트동아리")
                .clubEnglishName("testClub")
                .clubGroupChatLink("https://club-group-chat-link.com")
                .school(school)
                .build();
        clubRepository.save(club);

        Member member1 = Member.builder()
                .memberProvideId("testProvideId")
                .memberName("일반 회원 이름")
                .memberEmail("user@ajou.ac.kr")
                .school(school)
                .build();

        Member member2 = Member.builder()
                .memberProvideId("testProvideId2")
                .memberName("회장 회원 이름")
                .memberEmail("president@ajou.ac.kr")
                .school(school)
                .build();
        memberRepository.save(member1);
        memberRepository.save(member2);

        ClubMember clubMember1 = ClubMember.builder()
                .clubMemberAssignedTerm(LocalDate.now())
                .club(club)
                .member(member1)
                .clubMemberRole(MEMBER)
                .build();

        ClubMember clubMember2 = ClubMember.builder()
                .clubMemberAssignedTerm(LocalDate.now())
                .club(club)
                .member(member2)
                .clubMemberRole(OFFICER)
                .build();
        clubMemberRepository.save(clubMember1);
        clubMemberRepository.save(clubMember2);

        // When
        Boolean result1 = clubMemberRepository.existsByClubAndMemberAndClubMemberRole(club, member1, MEMBER);
        Boolean result2 = clubMemberRepository.existsByClubAndMemberAndClubMemberRole(club, member1, PRESIDENT);
        Boolean result3 = clubMemberRepository.existsByClubAndMemberAndClubMemberRole(club, member1, OFFICER);
        Boolean result4 = clubMemberRepository.existsByClubAndMemberAndClubMemberRole(club, member2, MEMBER);
        Boolean result5 = clubMemberRepository.existsByClubAndMemberAndClubMemberRole(club, member2, PRESIDENT);
        Boolean result6 = clubMemberRepository.existsByClubAndMemberAndClubMemberRole(club, member2, OFFICER);

        // Then
        assertThat(result1).isTrue();
        assertThat(result2).isFalse();
        assertThat(result3).isFalse();
        assertThat(result4).isFalse();
        assertThat(result5).isFalse();
        assertThat(result6).isTrue();
    }

}