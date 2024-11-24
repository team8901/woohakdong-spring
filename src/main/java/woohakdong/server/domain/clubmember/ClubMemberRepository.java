package woohakdong.server.domain.clubmember;

import java.time.LocalDate;
import java.util.List;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.member.Member;
import woohakdong.server.domain.school.School;

public interface ClubMemberRepository {
    ClubMember save(ClubMember clubMember);

    ClubMember getById(Long clubMemberId);

    ClubMember getByClubAndMember(Club club, Member member);

    List<ClubMember> getAllByMember(Member member);

    List<ClubMember> getAll();

    Boolean existsByClubAndMemberAndClubMemberRole(Club club, Member member, ClubMemberRole clubMemberRole);

    Boolean existsByClubAndMember(Club club, Member member);

    ClubMember getByClubAndMemberAndAssignedTerm(Club club, Member member, LocalDate assignedTerm);

    List<ClubMember> getAllBySearchFilter(Club club, String name, LocalDate assignedTerm);

    Integer countByClubAndAssignedTerm(Club club, LocalDate assignedTerm);

    Long countByClubMemberAssignedTerm(LocalDate clubMemberAssignedTerm);

    Long count();

    Long countByClub_School(School school);

    Long countByClub_SchoolAndClubMemberAssignedTerm(School school, LocalDate clubMemberAssignedTerm);
}
