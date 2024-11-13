package woohakdong.server.api.controller.admin.school;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import woohakdong.server.api.controller.ListWrapperResponse;
import woohakdong.server.api.controller.admin.overall.dto.ClubListResponse;
import woohakdong.server.api.controller.admin.overall.dto.CountResponse;
import woohakdong.server.api.service.admin.club.AdminSchoolService;

import java.util.List;

@RestController
@RequestMapping("/v1/admin/schools")
@RequiredArgsConstructor
public class AdminSchoolController implements AdminSchoolControllerDocs {
    private final AdminSchoolService adminSchoolService;

    @GetMapping("/{schoolId}/clubs/count")
    public CountResponse getClubCountBySchool(@PathVariable Long schoolId) {
        return adminSchoolService.getClubCountBySchool(schoolId);
    }

    @GetMapping("/{schoolId}/members/count")
    public CountResponse getMemberCountBySchool(@PathVariable Long schoolId) {
        return adminSchoolService.getMemberCountBySchool(schoolId);
    }

    @GetMapping("/{schoolId}/items/count")
    public CountResponse getItemCountBySchool(@PathVariable Long schoolId) {
        return adminSchoolService.getItemCountBySchool(schoolId);
    }

    @GetMapping("/{schoolId}/clubs")
    public ListWrapperResponse<ClubListResponse> getClubListBySchool(@PathVariable Long schoolId) {
        return ListWrapperResponse.of(adminSchoolService.getClubListBySchool(schoolId));
    }
}
