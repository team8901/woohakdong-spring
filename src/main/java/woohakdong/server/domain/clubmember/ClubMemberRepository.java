package woohakdong.server.domain.clubmember;

import org.springframework.data.jpa.repository.JpaRepository;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.member.Member;

public interface ClubMemberRepository extends JpaRepository<ClubMember, Long> {

    Boolean existsByClubAndMemberAndClubMemberRole(Club club, Member member, ClubMemberRole clubMemberRole);

    Boolean existsByClubAndMember(Club club, Member member);
}
