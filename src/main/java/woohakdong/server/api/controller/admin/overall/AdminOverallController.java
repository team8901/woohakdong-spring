package woohakdong.server.api.controller.admin.overall;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import woohakdong.server.api.controller.ListWrapperResponse;
import woohakdong.server.api.controller.admin.overall.dto.ClubListResponse;
import woohakdong.server.api.controller.admin.overall.dto.ClubPaymentResponse;
import woohakdong.server.api.controller.admin.overall.dto.CountResponse;
import woohakdong.server.api.controller.admin.overall.dto.SchoolListResponse;
import woohakdong.server.api.service.admin.overall.AdminOverallService;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.school.School;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/v1/admin")
@RequiredArgsConstructor
public class AdminOverallController implements AdminOverallControllerDocs{

    private final AdminOverallService adminOverallService;

    @GetMapping("/schools/count")
    public CountResponse getTotalSchoolCount(@RequestParam(required = false)
                                                 LocalDate assignedTerm) {
        return adminOverallService.getTotalSchoolCount(assignedTerm);
    }

    @GetMapping("/clubs/count")
    public CountResponse getTotalClubCount(@RequestParam(required = false)
                                               LocalDate assignedTerm) {
        return adminOverallService.getTotalClubCount(assignedTerm);
    }

    @GetMapping("/schools")
    public ListWrapperResponse<SchoolListResponse> getAllSchools(@RequestParam(required = false)
                                                                     LocalDate assignedTerm) {
        return ListWrapperResponse.of(adminOverallService.getAllSchools(assignedTerm));
    }

    @GetMapping("/clubs")
    public ListWrapperResponse<ClubListResponse> getAllClubs(@RequestParam(required = false)
                                                                 LocalDate assignedTerm) {
        return ListWrapperResponse.of(adminOverallService.getAllClubs(assignedTerm));
    }

    @GetMapping("/members/count")
    public CountResponse getTotalMemberCount(@RequestParam(required = false)
                                                 LocalDate assignedTerm) {
        return adminOverallService.getTotalMemberCount(assignedTerm);
    }

    @GetMapping("/clubPayments")
    public ClubPaymentResponse getClubPaymentByTerm(@RequestParam(required = false)
                                                    LocalDate assignedTerm) {
        return adminOverallService.getClubPaymentByTerm(assignedTerm);
    }
}
