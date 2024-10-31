package woohakdong.server.api.service.clubMember;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import woohakdong.server.api.controller.clubMember.dto.ClubMemberInfoResponse;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.club.ClubRepository;
import woohakdong.server.domain.clubmember.ClubMember;
import woohakdong.server.domain.clubmember.ClubMemberRepository;
import woohakdong.server.domain.clubmember.ClubMemberRole;
import woohakdong.server.domain.member.Member;
import woohakdong.server.domain.member.MemberRepository;
import woohakdong.server.domain.school.School;
import woohakdong.server.domain.school.SchoolRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static woohakdong.server.domain.clubmember.ClubMemberRole.MEMBER;
import static woohakdong.server.domain.clubmember.ClubMemberRole.OFFICER;

import java.time.LocalDate;
import java.util.List;


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

    @DisplayName("동아리 멤버 리스트를 확인할 수 있다.")
    @Test
    void getMembers() {
        // given
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
        memberRepository.saveAll(List.of(member1, member2));

        ClubMember clubMember1 = ClubMember.builder()
                .clubMemberAssignedTerm(getAssignedTerm())
                .club(club)
                .member(member1)
                .clubMemberRole(MEMBER)
                .build();

        ClubMember clubMember2 = ClubMember.builder()
                .clubMemberAssignedTerm(getAssignedTerm())
                .club(club)
                .member(member2)
                .clubMemberRole(OFFICER)
                .build();
        clubMemberRepository.saveAll(List.of(clubMember1, clubMember2));

        // when
        List<ClubMemberInfoResponse> responses = clubMemberService.getMembers(club.getClubId());

        System.out.println(responses);

        // then
        assertThat(responses).isNotNull();
        assertThat(responses.size()).isEqualTo(2);
    }

    @DisplayName("분기별 동아리 멤버 리스트를 확인할 수 있다.")
    @Test
    void getTermMembers() {
        // given
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
        memberRepository.saveAll(List.of(member1, member2));

        ClubMember clubMember1 = ClubMember.builder()
                .clubMemberAssignedTerm(getAssignedTerm())
                .club(club)
                .member(member1)
                .clubMemberRole(MEMBER)
                .build();

        ClubMember clubMember2 = ClubMember.builder()
                .clubMemberAssignedTerm(getAssignedTerm())
                .club(club)
                .member(member2)
                .clubMemberRole(OFFICER)
                .build();
        clubMemberRepository.saveAll(List.of(clubMember1, clubMember2));

        // when
        List<ClubMemberInfoResponse> responses = clubMemberService.getTermMembers(club.getClubId(), getAssignedTerm());

        // then
        assertThat(responses).isNotNull();
        assertThat(responses.size()).isEqualTo(2);
    }

    private LocalDate getAssignedTerm() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int semester = now.getMonthValue() <= 6 ? 1 : 7; // 1: 1학기, 7: 2학기
        return LocalDate.of(year, semester, 1);
    }
}