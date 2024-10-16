package woohakdong.server.api.controller.clubMember;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
    public List<ClubMemberInfoResponse> getMembers(@PathVariable Long clubId) {

        return clubMemberService.getMembers(clubId);
    }

    @GetMapping("/{clubId}/members/{clubMemberAssignedTerm}")
    public List<ClubMemberInfoResponse> getTermMembers(@PathVariable Long clubId, @PathVariable LocalDate clubMemberAssignedTerm) {

        return clubMemberService.getTermMembers(clubId, clubMemberAssignedTerm);
    }

}
