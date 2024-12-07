package woohakdong.server.api.controller.admin.club;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import woohakdong.server.api.controller.ListWrapperResponse;
import woohakdong.server.api.controller.admin.club.dto.ClubMemberResponse;
import woohakdong.server.api.controller.admin.club.dto.ClubStartDateResponse;
import woohakdong.server.api.controller.admin.club.dto.AdminItemHistoryResponse;
import woohakdong.server.api.controller.admin.overall.dto.ClubPaymentResponse;
import woohakdong.server.api.controller.admin.overall.dto.CountResponse;
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

    @GetMapping("/{clubId}/items/count")
    public CountResponse getClubItemCount(@PathVariable Long clubId,
                                          @RequestParam(required = false) LocalDate assignedTerm) {
        return adminClubService.getClubItemCount(clubId, assignedTerm);
    }

    @GetMapping("/{clubId}/period")
    public ClubStartDateResponse getClubOperationPeriod(@PathVariable Long clubId) {
        return adminClubService.getClubOperationPeriod(clubId);
    }

    @GetMapping("/{clubId}/itemHistory")
    public ListWrapperResponse<AdminItemHistoryResponse> getItemHistory(@PathVariable Long clubId,
                                                   @RequestParam(required = false) LocalDate assignedTerm) {
        return ListWrapperResponse.of(adminClubService.getItemHistory(clubId, assignedTerm));
    }

    @GetMapping("/{clubId}/clubPayments")
    public ClubPaymentResponse getClubPaymentByTerm(@PathVariable Long clubId,
                                                    @RequestParam(required = false) LocalDate assignedTerm) {
        return adminClubService.getClubPaymentByTerm(clubId, assignedTerm);
    }
}
