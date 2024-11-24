package woohakdong.server.domain.clubmember;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.member.Member;
import woohakdong.server.domain.school.School;

import java.time.LocalDate;
import java.util.List;

public interface ClubMemberJpaRepository extends JpaRepository<ClubMember, Long> {

    Boolean existsByClubAndMemberAndClubMemberRole(Club club, Member member, ClubMemberRole clubMemberRole);

    Boolean existsByClubAndMember(Club club, Member member);

    List<ClubMember> findByClubAndClubMemberAssignedTerm(Club club, LocalDate assignedTerm);

    List<ClubMember> findAllByMember(Member member);

    Optional<ClubMember> findByClubAndMemberAndClubMemberAssignedTerm(Club club, Member member, LocalDate assignedTerm);

    Optional<ClubMember> findByClubAndMember(Club club, Member member);

    List<ClubMember> findByClubAndClubMemberAssignedTermAndMemberMemberNameContaining(Club club, LocalDate assignedTerm, String name);

    Integer countByClubAndClubMemberAssignedTerm(Club club, LocalDate assignedTerm);

    Long countByClubMemberAssignedTerm(LocalDate clubMemberAssignedTerm);

    Long countByClub_School(School school);

    Long countByClub_SchoolAndClubMemberAssignedTerm(School school, LocalDate clubMemberAssignedTerm);
}
