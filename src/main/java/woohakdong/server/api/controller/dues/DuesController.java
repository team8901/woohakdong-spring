package woohakdong.server.api.controller.dues;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.*;
import woohakdong.server.api.controller.ListWrapperResponse;
import woohakdong.server.api.controller.SliceResponse;
import woohakdong.server.api.controller.clubMember.dto.ClubMemberInfoResponse;
import woohakdong.server.api.controller.dues.dto.ClubAccountHistoryListResponse;
import woohakdong.server.api.service.dues.DuesService;
import woohakdong.server.domain.clubAccountHistory.ClubAccountHistory;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/clubs")
public class DuesController implements DuesControllerDocs {

    private final DuesService duesService;

    @PostMapping("/{clubId}/dues/update")
    public void updateTransactions(@PathVariable Long clubId) {
        duesService.fetchAndSaveRecentTransactions(clubId);
    }

//    @Override
//    public ListWrapperResponse<ClubAccountHistoryListResponse> getMonthlyTransactions(Long clubId, LocalDate date) {
//        return null;
//    }

    @GetMapping("/{clubId}/dues")
    public SliceResponse<ClubAccountHistoryListResponse> getMonthlyTransactions(
            @PathVariable Long clubId,
            @RequestParam(required = false) LocalDate date,
            @RequestParam(required = false) String keyword,
            Pageable pageable) {

        Slice<ClubAccountHistoryListResponse> historys = duesService.getMonthlyTransactions(clubId, date, keyword, pageable);
        return SliceResponse.of(historys.getContent(), historys.getNumber(), historys.hasNext());
    }
}
