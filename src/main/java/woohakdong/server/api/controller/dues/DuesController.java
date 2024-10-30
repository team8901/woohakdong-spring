package woohakdong.server.api.controller.dues;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import woohakdong.server.api.controller.ListWrapperResponse;
import woohakdong.server.api.controller.clubMember.dto.ClubMemberInfoResponse;
import woohakdong.server.api.controller.dues.dto.ClubAccountHistoryListResponse;
import woohakdong.server.api.service.dues.DuesService;
import woohakdong.server.domain.clubAccountHistory.ClubAccountHistory;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/club")
public class DuesController implements DuesControllerDocs {

    private final DuesService duesService;

    @PostMapping("/{clubId}/dues/update")
    public void updateTransactions(@PathVariable Long clubId) {
        duesService.fetchAndSaveRecentTransactions(clubId);
    }

    @GetMapping("/{clubId}/dues")
    public ListWrapperResponse<ClubAccountHistoryListResponse> getMonthlyTransactions(
            @PathVariable Long clubId,
            @RequestParam int year,
            @RequestParam int month) {

        List<ClubAccountHistoryListResponse> historys = duesService.getMonthlyTransactions(clubId, year, month);
        return ListWrapperResponse.of(historys);
    }
}
