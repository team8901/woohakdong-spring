package woohakdong.server.api.service.admin.overall;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woohakdong.server.api.controller.admin.overall.dto.ClubListResponse;
import woohakdong.server.api.controller.admin.overall.dto.CountResponse;
import woohakdong.server.api.controller.admin.overall.dto.SchoolListResponse;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.club.ClubRepository;
import woohakdong.server.domain.member.MemberRepository;
import woohakdong.server.domain.school.School;
import woohakdong.server.domain.school.SchoolRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminOverallService {

    private final SchoolRepository schoolRepository;
    private final ClubRepository clubRepository;
    private final MemberRepository memberRepository;

    public CountResponse getTotalSchoolCount() {
        return CountResponse.from(schoolRepository.count());
    }

    public List<SchoolListResponse> getAllSchools() {
        List<School> schools = schoolRepository.getAll();
        return schools.stream()
                .map(school -> SchoolListResponse.from(school))
                .collect(Collectors.toList());
    }

    public CountResponse getTotalClubCount() {
        return CountResponse.from(clubRepository.count());
    }

    public List<ClubListResponse> getAllClubs() {
        List<Club> clubs = clubRepository.getAll();
        List<ClubListResponse> clubListResponses = clubs.stream()
                .map(club -> ClubListResponse.from(club, club.getSchool()))
                .collect(Collectors.toList());

        return clubListResponses;
    }

    public CountResponse getTotalMemberCount() {
        return CountResponse.from(memberRepository.count());
    }
}
