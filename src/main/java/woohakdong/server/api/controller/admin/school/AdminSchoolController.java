package woohakdong.server.api.controller.admin.school;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import woohakdong.server.api.controller.ListWrapperResponse;
import woohakdong.server.api.controller.admin.overall.dto.ClubListResponse;
import woohakdong.server.api.controller.admin.overall.dto.CountResponse;
import woohakdong.server.api.service.admin.school.AdminSchoolService;

import java.time.LocalDate;


@RestController
@RequestMapping("/v1/admin/schools")
@RequiredArgsConstructor
public class AdminSchoolController implements AdminSchoolControllerDocs {
    private final AdminSchoolService adminSchoolService;

    @GetMapping("/{schoolId}/clubs/count")
    public CountResponse getClubCountBySchool(@PathVariable Long schoolId,
                                              @RequestParam(required = false) LocalDate assignedTerm) {
        return adminSchoolService.getClubCountBySchool(schoolId, assignedTerm);
    }

    @GetMapping("/{schoolId}/members/count")
    public CountResponse getMemberCountBySchool(@PathVariable Long schoolId,
                                                @RequestParam(required = false) LocalDate assignedTerm) {
        return adminSchoolService.getMemberCountBySchool(schoolId, assignedTerm);
    }

    @GetMapping("/{schoolId}/items/count")
    public CountResponse getItemCountBySchool(@PathVariable Long schoolId,
                                              @RequestParam(required = false) LocalDate assignedTerm) {
        return adminSchoolService.getItemCountBySchool(schoolId, assignedTerm);
    }

    @GetMapping("/{schoolId}/clubs")
    public ListWrapperResponse<ClubListResponse> getClubListBySchool(@PathVariable Long schoolId,
                                                                     @RequestParam(required = false) LocalDate assignedTerm) {
        return ListWrapperResponse.of(adminSchoolService.getClubListBySchool(schoolId, assignedTerm));
    }
}
