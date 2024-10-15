package woohakdong.server.domain.clubmember;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.member.Member;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ClubMemberRepository extends JpaRepository<ClubMember, Long> {

    Boolean existsByClubAndMemberAndClubMemberRole(Club club, Member member, ClubMemberRole clubMemberRole);

    @Query("SELECT cm FROM ClubMember cm WHERE cm.club.clubId = :clubId AND cm.clubMemberAssignedTerm = :assignedTerm")
    Optional<List<ClubMember>> findByClubIdAndAssignedTerm(@Param("clubId") Long clubId, @Param("assignedTerm") LocalDate assignedTerm);
}
