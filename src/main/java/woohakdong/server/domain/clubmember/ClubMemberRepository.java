package woohakdong.server.domain.clubmember;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.repository.query.Param;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.member.Member;

public interface ClubMemberRepository {
    ClubMember save(ClubMember clubMember);

    List<ClubMember> getAllByMember(Member member);

    List<ClubMember> getAll();

    Boolean existsByClubAndMemberAndClubMemberRole(Club club, Member member, ClubMemberRole clubMemberRole);

    Boolean existsByClubAndMember(Club club, Member member);


    List<ClubMember> getByClubIdAndAssignedTerm(Club club, LocalDate assignedTerm);

    List<ClubMember> findByClubAndClubMemberAssignedTerm(Club club, LocalDate assignedTerm);
}
