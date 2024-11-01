package woohakdong.server.domain.clubmember;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.member.Member;

import java.time.LocalDate;
import java.util.List;

public interface ClubMemberJpaRepository extends JpaRepository<ClubMember, Long> {

    Boolean existsByClubAndMemberAndClubMemberRole(Club club, Member member, ClubMemberRole clubMemberRole);

    Boolean existsByClubAndMember(Club club, Member member);

    List<ClubMember> findByClubAndClubMemberAssignedTerm(Club club, LocalDate assignedTerm);

    List<ClubMember> findAllByMember(Member member);
}
