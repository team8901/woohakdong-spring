package woohakdong.server.api.controller.admin.overall;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import woohakdong.server.api.controller.ListWrapperResponse;
import woohakdong.server.api.controller.admin.overall.dto.ClubListResponse;
import woohakdong.server.api.controller.admin.overall.dto.CountResponse;
import woohakdong.server.api.controller.admin.overall.dto.SchoolListResponse;
import woohakdong.server.api.service.admin.overall.AdminOverallService;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.school.School;

import java.util.List;

@RestController
@RequestMapping("/v1/admin")
@RequiredArgsConstructor
public class AdminOverallController implements AdminOverallControllerDocs{

    private final AdminOverallService adminOverallService;

    @GetMapping("/schools/count")
    public CountResponse getTotalSchoolCount() {
        return adminOverallService.getTotalSchoolCount();
    }

    @GetMapping("/clubs/count")
    public CountResponse getTotalClubCount() {
        return adminOverallService.getTotalClubCount();
    }

    @GetMapping("/schools")
    public ListWrapperResponse<SchoolListResponse> getAllSchools() {
        return ListWrapperResponse.of(adminOverallService.getAllSchools());
    }

    @GetMapping("/clubs")
    public ListWrapperResponse<ClubListResponse> getAllClubs() {
        return ListWrapperResponse.of(adminOverallService.getAllClubs());
    }

    @GetMapping("/members/count")
    public CountResponse getTotalMemberCount() {
        return adminOverallService.getTotalMemberCount();
    }
}
