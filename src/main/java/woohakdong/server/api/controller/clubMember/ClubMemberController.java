package woohakdong.server.api.controller.clubMember;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import woohakdong.server.api.controller.ListWrapperResponse;
import woohakdong.server.api.controller.SliceResponse;
import woohakdong.server.api.controller.clubMember.dto.ClubMemberInfoResponse;
import woohakdong.server.api.service.clubMember.ClubMemberService;
import woohakdong.server.domain.clubmember.ClubMemberRole;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/clubs")
public class ClubMemberController implements ClubMemberControllerDocs {

    private final ClubMemberService clubMemberService;

    @GetMapping("/{clubId}/members")
    public SliceResponse<ClubMemberInfoResponse> getFilteredMembers(@PathVariable Long clubId,
                                                                    @RequestParam(required = false) LocalDate clubMemberAssignedTerm,
                                                                    @RequestParam(required = false) String name,
                                                                    Pageable pageable) {
        if (clubMemberAssignedTerm == null) {
            clubMemberAssignedTerm = LocalDate.now();
        }
        Slice<ClubMemberInfoResponse> responses = clubMemberService.getFilteredMembers(clubId, name, clubMemberAssignedTerm, pageable);

        return SliceResponse.of(responses.getContent(), responses.getNumber(), responses.hasNext());
    }

    @GetMapping("/{clubId}/members/{clubMemberId}")
    public ClubMemberInfoResponse getClubMemberInfo(@PathVariable Long clubId, @PathVariable Long clubMemberId) {
        return clubMemberService.getClubMemberInfo(clubId, clubMemberId);
    }

    @GetMapping("/{clubId}/members/me")
    public ClubMemberInfoResponse getMyInfo(@PathVariable Long clubId) {
        return clubMemberService.getMyInfo(clubId, LocalDate.now());
    }

    @PutMapping("/{clubId}/members/{clubMemberId}/role")
    public void changeRole(@PathVariable Long clubId,
                           @PathVariable Long clubMemberId,
                           @RequestParam ClubMemberRole clubMemberRole) {
        clubMemberService.changeClubMemberRole(clubId, clubMemberId, clubMemberRole, LocalDate.now());
    }

    @PostMapping("/{clubId}/members/{clubMemberId}/presidency")
    public void passOnThePresidency(@PathVariable Long clubId,
                                    @PathVariable Long clubMemberId) {
        clubMemberService.passOnThePresidency(clubId, clubMemberId, LocalDate.now());
    }
}
