package woohakdong.server.api.controller.admin.club;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import woohakdong.server.api.controller.ListWrapperResponse;
import woohakdong.server.api.controller.admin.club.dto.ClubMemberResponse;
import woohakdong.server.api.controller.admin.club.dto.ClubStartDateResponse;
import woohakdong.server.api.controller.admin.overall.dto.CountResponse;
import woohakdong.server.api.controller.clubMember.dto.ClubMemberInfoResponse;
import woohakdong.server.api.service.admin.club.AdminClubService;

import java.time.LocalDate;

@RestController
@RequestMapping("/v1/admin/clubs")
@RequiredArgsConstructor
public class AdminClubController implements AdminClubControllerDocs{
    private final AdminClubService adminClubService;

    @GetMapping("/{clubId}/members")
    public ListWrapperResponse<ClubMemberResponse> getClubMembers(@PathVariable Long clubId,
                                                                  @RequestParam(required = false) LocalDate assignedTerm
    ) {
        return ListWrapperResponse.of(adminClubService.getClubMembers(clubId, assignedTerm));
    }

    // 2. 동아리별 등록된 물품 수 반환
    @GetMapping("/{clubId}/items/count")
    public CountResponse getClubItemCount(@PathVariable Long clubId,
                                          @RequestParam(required = false) LocalDate assignedTerm) {
        return adminClubService.getClubItemCount(clubId, assignedTerm);
    }

    // 3. 동아리 운영 기간 반환
    @GetMapping("/{clubId}/operation-period")
    public ClubStartDateResponse getClubOperationPeriod(@PathVariable Long clubId) {
        return adminClubService.getClubOperationPeriod(clubId);
    }
}
