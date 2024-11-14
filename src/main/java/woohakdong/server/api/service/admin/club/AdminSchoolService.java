package woohakdong.server.api.service.admin.club;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woohakdong.server.api.controller.admin.overall.dto.ClubListResponse;
import woohakdong.server.api.controller.admin.overall.dto.CountResponse;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.club.ClubRepository;
import woohakdong.server.domain.item.ItemRepository;
import woohakdong.server.domain.member.MemberRepository;
import woohakdong.server.domain.school.School;
import woohakdong.server.domain.school.SchoolRepository;

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

    public CountResponse getClubCountBySchool(Long schoolId) {
        School school = schoolRepository.getById(schoolId);

        Long clubCount = clubRepository.countBySchool(school);
        return CountResponse.from(clubCount);
    }

    public CountResponse getMemberCountBySchool(Long schoolId) {
        School school = schoolRepository.getById(schoolId);

        Long memberCount = memberRepository.countBySchool(school);
        return CountResponse.from(memberCount);
    }

    public CountResponse getItemCountBySchool(Long schoolId) {
        School school = schoolRepository.getById(schoolId);

        Long itemCount = itemRepository.countByClubSchool(school);
        return CountResponse.from(itemCount);
    }

    public List<ClubListResponse> getClubListBySchool(Long schoolId) {
        School school = schoolRepository.getById(schoolId);

        List<Club> clubs = clubRepository.getBySchool(school);
        return clubs.stream()
                .map(club -> ClubListResponse.from(club, club.getSchool()))
                .collect(Collectors.toList());
    }
}
