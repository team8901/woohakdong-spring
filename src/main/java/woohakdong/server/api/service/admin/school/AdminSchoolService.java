package woohakdong.server.api.service.admin.school;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woohakdong.server.api.controller.admin.overall.dto.ClubListResponse;
import woohakdong.server.api.controller.admin.overall.dto.CountResponse;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.club.ClubRepository;
import woohakdong.server.domain.clubHistory.ClubHistoryRepository;
import woohakdong.server.domain.clubmember.ClubMemberRepository;
import woohakdong.server.domain.item.ItemRepository;
import woohakdong.server.domain.member.MemberRepository;
import woohakdong.server.domain.school.School;
import woohakdong.server.domain.school.SchoolRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminSchoolService {

    private final ClubRepository clubRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final SchoolRepository schoolRepository;
    private final ClubHistoryRepository clubHistoryRepository;
    private final ClubMemberRepository clubMemberRepository;

    public CountResponse getClubCountBySchool(Long schoolId, LocalDate assignedTerm) {
        School school = schoolRepository.getById(schoolId);
        Long clubCount;
        if (assignedTerm == null) {
            clubCount = clubRepository.countBySchool(school);
        } else {
            clubCount = clubHistoryRepository.countByClub_SchoolAndClubHistoryUsageDate(school, assignedTerm);
        }
        return CountResponse.from(clubCount);
    }

    public CountResponse getMemberCountBySchool(Long schoolId, LocalDate assignedTerm) {
        School school = schoolRepository.getById(schoolId);

        Long memberCount;
        if (assignedTerm == null) {
            memberCount = clubMemberRepository.countByClub_School(school);
        } else {
            memberCount = clubMemberRepository.countByClub_SchoolAndClubMemberAssignedTerm(school, assignedTerm);
        }
        return CountResponse.from(memberCount);
    }

    public CountResponse getItemCountBySchool(Long schoolId, LocalDate assignedTerm) {
        School school = schoolRepository.getById(schoolId);

        Long itemCount;
        if (assignedTerm == null) {
            itemCount = itemRepository.countByClubSchool(school);
        } else {
            itemCount = itemRepository.countByClubSchoolAndCreatedAtBefore(school, assignedTerm.plusMonths(6).atStartOfDay());
        }
        return CountResponse.from(itemCount);
    }

    public List<ClubListResponse> getClubListBySchool(Long schoolId, LocalDate assignedTerm) {
        School school = schoolRepository.getById(schoolId);

        List<Club> clubs;
        if (assignedTerm == null) {
            clubs = clubRepository.getBySchool(school);
        } else {
            clubs = clubHistoryRepository.getDistinctClubsByClubHistoryUsageDateAndSchool(assignedTerm, school);
        }
        return clubs.stream()
                .map(club -> ClubListResponse.from(club, club.getSchool()))
                .collect(Collectors.toList());
    }
}
