package woohakdong.server.api.service.admin.overall;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woohakdong.server.api.controller.admin.overall.dto.ClubListResponse;
import woohakdong.server.api.controller.admin.overall.dto.CountResponse;
import woohakdong.server.api.controller.admin.overall.dto.SchoolListResponse;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.club.ClubRepository;
import woohakdong.server.domain.clubHistory.ClubHistoryRepository;
import woohakdong.server.domain.clubmember.ClubMemberRepository;
import woohakdong.server.domain.member.MemberRepository;
import woohakdong.server.domain.school.School;
import woohakdong.server.domain.school.SchoolRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminOverallService {

    private final SchoolRepository schoolRepository;
    private final ClubRepository clubRepository;
    private final MemberRepository memberRepository;
    private final ClubHistoryRepository clubHistoryRepository;
    private final ClubMemberRepository clubMemberRepository;

    public CountResponse getTotalSchoolCount(LocalDate assignedTerm) {
        if (assignedTerm == null) {
            return CountResponse.from(schoolRepository.count());
        }
        Long count = schoolRepository.countByCreatedAtAfter(assignedTerm.atStartOfDay());
        return CountResponse.from(count);
    }

    public List<SchoolListResponse> getAllSchools(LocalDate assignedTerm) {
        if (assignedTerm == null) {
            List<School> schools = schoolRepository.getAll();
            return schools.stream()
                    .map(SchoolListResponse::from)
                    .collect(Collectors.toList());
        }
        List<School> schools = schoolRepository.getByCreatedAtAfter(assignedTerm.atStartOfDay());
        return schools.stream()
                .map(SchoolListResponse::from)
                .collect(Collectors.toList());
    }

    public CountResponse getTotalClubCount(LocalDate assignedTerm) {
        if (assignedTerm == null) {
            return CountResponse.from(clubRepository.count());
        }
        Long count = clubHistoryRepository.countByClubHistoryUsageDate(assignedTerm);
        return CountResponse.from(count);
    }

    public List<ClubListResponse> getAllClubs(LocalDate assignedTerm) {
        List<Club> clubs;
        if (assignedTerm == null) {
            clubs = clubRepository.getAll();
        } else {
            clubs = clubHistoryRepository.getDistinctClubByClubHistoryUsageDate(assignedTerm);
        }
        return clubs.stream()
                .map(club -> ClubListResponse.from(club, club.getSchool()))
                .collect(Collectors.toList());
    }

    public CountResponse getTotalMemberCount(LocalDate assignedTerm) {
        if (assignedTerm == null) {
            return CountResponse.from(clubMemberRepository.count());
        }
        Long count = clubMemberRepository.countByClubMemberAssignedTerm(assignedTerm);
        return CountResponse.from(count);
    }
}
