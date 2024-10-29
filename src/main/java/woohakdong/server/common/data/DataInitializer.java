package woohakdong.server.common.data;

import static woohakdong.server.domain.member.MemberGender.MAN;
import static woohakdong.server.domain.member.MemberGender.WOMAN;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import woohakdong.server.domain.admin.adminAccount.AdminAccount;
import woohakdong.server.domain.admin.adminAccount.AdminAccountRepository;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.club.ClubRepository;
import woohakdong.server.domain.clubmember.ClubMember;
import woohakdong.server.domain.clubmember.ClubMemberRepository;
import woohakdong.server.domain.clubmember.ClubMemberRole;
import woohakdong.server.domain.group.Group;
import woohakdong.server.domain.group.GroupRepository;
import woohakdong.server.domain.group.GroupType;
import woohakdong.server.domain.member.Member;
import woohakdong.server.domain.member.MemberGender;
import woohakdong.server.domain.member.MemberRepository;
import woohakdong.server.domain.school.School;
import woohakdong.server.domain.school.SchoolRepository;

@RequiredArgsConstructor
@Component
public class DataInitializer implements CommandLineRunner {

    private final SchoolRepository schoolRepository;
    private final AdminAccountRepository adminAccountRepository;
    private final ClubRepository clubRepository;
    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;
    private final ClubMemberRepository clubMemberRepository;

    @Override
    public void run(String... args) throws Exception {
        if (schoolRepository.count() == 0) {
            School school = School.builder()
                    .schoolName("아주대학교")
                    .schoolDomain("ajou.ac.kr")
                    .build();
            schoolRepository.save(school);

            AdminAccount adminAccount = AdminAccount.builder()
                    .adminAccountBankCode("011")
                    .adminAccountAmount(10000000L)
                    .adminAccountBankName("농협은행")
                    .adminAccountNumber("3020000011527")
                    .build();
            adminAccountRepository.save(adminAccount);

            Member member1 = Member.builder()
                    .memberEmail("sangjun@ajou.ac.kr")
                    .memberRole("ROLE_USER")
                    .memberName("박상준")
                    .memberProvideId("google_test")
                    .memberMajor("소프트웨어학과")
                    .memberPhoneNumber("01012345678")
                    .memberStudentNumber("202020736")
                    .memberGender(MAN)
                    .school(school)
                    .build();

            Member member2 = Member.builder()
                    .memberEmail("junpark@ajou.ac.kr")
                    .memberRole("ROLE_USER")
                    .memberName("박준")
                    .memberProvideId("google_test2")
                    .memberMajor("소프트웨어학과")
                    .memberPhoneNumber("01011115678")
                    .memberStudentNumber("202020737")
                    .memberGender(MAN)
                    .school(school)
                    .build();

            Member member3 = Member.builder()
                    .memberEmail("jiwon312@ajou.ac.kr")
                    .memberRole("ROLE_USER")
                    .memberName("김지원")
                    .memberProvideId("google_test3")
                    .memberMajor("디지털미디어학과")
                    .memberPhoneNumber("01012343333")
                    .memberStudentNumber("202020738")
                    .memberGender(WOMAN)
                    .school(school)
                    .build();
            memberRepository.saveAll(List.of(member1, member2, member3));

            Club club = Club.builder()
                    .school(school)
                    .clubName("두잇")
                    .clubEnglishName("doit")
                    .clubDues(10000)
                    .clubDescription("두잇 동아리")
                    .clubImage("https://s3.ap-northeast-2.amazonaws.com/woohakdong.image/Do-iT-LOGO.png")
                    .clubGeneration("34")
                    .clubRoom("구학생회관 201호")
                    .clubEstablishmentDate(LocalDate.of(2017, 7, 1))
                    .build();
            clubRepository.save(club);

            ClubMember clubMember = ClubMember.builder()
                    .club(club)
                    .member(member1)
                    .clubMemberRole(ClubMemberRole.PRESIDENT)
                    .clubMemberAssignedTerm(LocalDate.of(2024, 7, 1))
                    .clubJoinedDate(LocalDate.of(2024, 7, 1))
                    .build();
            clubMemberRepository.save(clubMember);

            ClubMember clubMember2 = ClubMember.builder()
                    .club(club)
                    .member(member2)
                    .clubMemberRole(ClubMemberRole.OFFICER)
                    .clubMemberAssignedTerm(LocalDate.of(2024, 7, 1))
                    .clubJoinedDate(LocalDate.of(2024, 7, 2))
                    .build();
            clubMemberRepository.save(clubMember2);

            ClubMember clubMember3 = ClubMember.builder()
                    .club(club)
                    .member(member3)
                    .clubMemberRole(ClubMemberRole.MEMBER)
                    .clubMemberAssignedTerm(LocalDate.of(2024, 7, 1))
                    .clubJoinedDate(LocalDate.of(2024, 7, 3))
                    .build();
            clubMemberRepository.save(clubMember3);

            Group group = Group.builder()
                    .groupName("두잇")
                    .groupDescription("두잇 가입 그룹")
                    .club(club)
                    .groupType(GroupType.JOIN)
                    .groupJoinLink("https://woohakdong.com/clubs/doit")
                    .groupChatLink("https://open.kakao.com/o/gUEMLKVg")
                    .groupChatPassword("1234")
                    .groupAmount(10000)
                    .build();
            groupRepository.save(group);
        }
    }
}
