package woohakdong.server.domain.clubmember;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.member.Member;

@RequiredArgsConstructor
@Repository
public class ClubMemberRepositoryImpl implements ClubMemberRepository {

    private final ClubMemberJpaRepository clubMemberJpaRepository;

    @Override
    public ClubMember save(ClubMember clubMember) {
        return clubMemberJpaRepository.save(clubMember);
    }

    @Override
    public List<ClubMember> getAllByMember(Member member) {
        return clubMemberJpaRepository.findAllByMember(member);
    }

    @Override
    public List<ClubMember> getAll() {
        return clubMemberJpaRepository.findAll();
    }

    @Override
    public Boolean existsByClubAndMemberAndClubMemberRole(Club club, Member member, ClubMemberRole clubMemberRole) {
        return clubMemberJpaRepository.existsByClubAndMemberAndClubMemberRole(club, member, clubMemberRole);
    }

    @Override
    public Boolean existsByClubAndMember(Club club, Member member) {
        return clubMemberJpaRepository.existsByClubAndMember(club, member);
    }

    @Override
    public List<ClubMember> getByClubIdAndAssignedTerm(Club club, LocalDate assignedTerm) {
        return clubMemberJpaRepository.findByClubAndClubMemberAssignedTerm(club, assignedTerm);
    }

    @Override
    public List<ClubMember> findByClubAndClubMemberAssignedTerm(Club club, LocalDate assignedTerm) {
        return clubMemberJpaRepository.findByClubAndClubMemberAssignedTerm(club, assignedTerm);
    }
}
