package woohakdong.server.common.data;

import java.time.LocalDate;
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
            schoolRepository.save(School.builder()
                    .schoolName("아주대학교")
                    .schoolDomain("ajou.ac.kr")
                    .build());
        }

        if (adminAccountRepository.count() == 0) {
            adminAccountRepository.save(AdminAccount.builder()
                    .adminAccountBankCode("011")
                    .adminAccountAmount(10000000L)
                    .adminAccountBankName("농협은행")
                    .adminAccountNumber("3020000011527")
                    .build());
        }

        if(clubRepository.count() == 0) {
            Member member = Member.builder()
                    .memberEmail("test@email.com")
                    .memberRole("ROLE_USER")
                    .memberName("test")
                    .memberProvideId("test")
                    .school(schoolRepository.findById(1L).get())
                    .build();
            memberRepository.save(member);

            Club club = Club.builder()
                    .school(schoolRepository.findById(1L).get())
                    .clubName("두잇")
                    .clubEnglishName("doit")
                    .clubDues(10000)
                    .clubDescription("두잇 동아리")
                    .build();
            clubRepository.save(club);

            ClubMember clubMember = ClubMember.builder()
                    .club(club)
                    .member(member)
                    .clubMemberRole(ClubMemberRole.PRESIDENT)
                    .clubMemberAssignedTerm(LocalDate.of(2024, 7, 1))
                    .build();
            clubMemberRepository.save(clubMember);

            Group group = Group.builder()
                    .groupName("두잇")
                    .groupDescription("두잇 가입 그룹")
                    .club(club)
                    .groupType(GroupType.JOIN)
                    .groupLink("http://woohakdong.com/clubs/doit")
                    .groupAmount(10000)
                    .build();
            groupRepository.save(group);
        }
    }
}
