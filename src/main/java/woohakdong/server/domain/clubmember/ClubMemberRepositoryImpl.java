package woohakdong.server.domain.clubmember;

import static woohakdong.server.common.exception.CustomErrorInfo.CLUB_MEMBER_NOT_FOUND;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;
import woohakdong.server.common.exception.CustomException;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.group.Group;
import woohakdong.server.domain.member.Member;
import woohakdong.server.domain.school.School;

@RequiredArgsConstructor
@Repository
public class ClubMemberRepositoryImpl implements ClubMemberRepository {

    private final ClubMemberJpaRepository clubMemberJpaRepository;

    @Override
    public ClubMember save(ClubMember clubMember) {
        return clubMemberJpaRepository.save(clubMember);
    }

    @Override
    public ClubMember getById(Long clubMemberId) {
        return clubMemberJpaRepository.findById(clubMemberId)
                .orElseThrow(() -> new CustomException(CLUB_MEMBER_NOT_FOUND));
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
    public ClubMember getByClubAndMemberAndAssignedTerm(Club club, Member member, LocalDate assignedTerm) {
        return clubMemberJpaRepository.findByClubAndMemberAndClubMemberAssignedTerm(club, member, assignedTerm)
                .orElseThrow(() -> new CustomException(CLUB_MEMBER_NOT_FOUND));
    }

    @Override
    public List<ClubMember> getAllBySearchFilter(Club club, String name, LocalDate assignedTerm) {
        if (name == null || name.isBlank()) {
            return clubMemberJpaRepository.findByClubAndClubMemberAssignedTerm(club, assignedTerm);
        }

        return clubMemberJpaRepository.findByClubAndClubMemberAssignedTermAndMemberMemberNameContaining(club,
                assignedTerm, name);
    }

    @Override
    public Integer countByClubAndAssignedTerm(Club club, LocalDate assignedTerm) {
        return clubMemberJpaRepository.countByClubAndClubMemberAssignedTerm(club, assignedTerm);
    }

    @Override
    public Long countByClubMemberAssignedTerm(LocalDate clubMemberAssignedTerm){
        return clubMemberJpaRepository.countByClubMemberAssignedTerm(clubMemberAssignedTerm);
    }

    @Override
    public Long count() {
        return clubMemberJpaRepository.count();
    }

    @Override
    public Long countByClub_School(School school) {
        return clubMemberJpaRepository.countByClub_School(school);
    }

    @Override
    public Long countByClub_SchoolAndClubMemberAssignedTerm(School school, LocalDate clubMemberAssignedTerm) {
        return clubMemberJpaRepository.countByClub_SchoolAndClubMemberAssignedTerm(school, clubMemberAssignedTerm);
    }

    @Override
    public List<ClubMember> getAllByClub(Club club) {
        return clubMemberJpaRepository.findAllByClub(club);
    }

    @Override
    public List<ClubMember> getByClubAndClubMemberAssignedTerm(Club club, LocalDate assignedTerm) {
        return clubMemberJpaRepository.findByClubAndClubMemberAssignedTerm(club, assignedTerm);
    }

    @Override
    public Slice<ClubMember> getAllBySearchFilterPaging(Club club, String name, LocalDate assignedTerm, Pageable pageable) {
        if (name == null || name.isBlank()) {
            return clubMemberJpaRepository.findByClubAndClubMemberAssignedTerm(club, assignedTerm, pageable);
        }

        return clubMemberJpaRepository.findByClubAndClubMemberAssignedTermAndMemberMemberNameContaining(club,
                assignedTerm, name, pageable);
    }
}
