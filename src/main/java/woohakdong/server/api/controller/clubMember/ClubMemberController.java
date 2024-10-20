package woohakdong.server.api.controller.clubMember;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import woohakdong.server.api.controller.ListWrapperResponse;
import woohakdong.server.api.controller.clubMember.dto.ClubMemberInfoResponse;
import woohakdong.server.api.service.clubMember.ClubMemberService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/club")
public class ClubMemberController implements ClubMemberControllerDocs{

    private final ClubMemberService clubMemberService;

    @GetMapping("/{clubId}/members")
    public ListWrapperResponse<ClubMemberInfoResponse> getTermMembers(@PathVariable Long clubId, @RequestParam(required = false) LocalDate clubMemberAssignedTerm) {

        if (clubMemberAssignedTerm != null) {
            // 학기로 필터링된 멤버 목록 조회
            return ListWrapperResponse.of(clubMemberService.getTermMembers(clubId, clubMemberAssignedTerm));
        } else {
            // 모든 멤버 조회
            return ListWrapperResponse.of(clubMemberService.getMembers(clubId));
        }
    }

}
