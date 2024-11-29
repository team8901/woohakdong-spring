package woohakdong.server.api.controller.club;

import jakarta.validation.Valid;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import woohakdong.server.api.controller.ListWrapperResponse;
import woohakdong.server.api.controller.club.dto.ClubAccountRegisterRequest;
import woohakdong.server.api.controller.club.dto.ClubAccountResponse;
import woohakdong.server.api.controller.club.dto.ClubAccountValidateRequest;
import woohakdong.server.api.controller.club.dto.ClubAccountValidateResponse;
import woohakdong.server.api.controller.club.dto.ClubCreateRequest;
import woohakdong.server.api.controller.club.dto.ClubHistoryTermResponse;
import woohakdong.server.api.controller.club.dto.ClubIdResponse;
import woohakdong.server.api.controller.club.dto.ClubInfoResponse;
import woohakdong.server.api.controller.club.dto.ClubNameValidateRequest;
import woohakdong.server.api.controller.club.dto.ClubSummaryResponse;
import woohakdong.server.api.controller.club.dto.ClubUpdateRequest;
import woohakdong.server.api.controller.group.dto.GroupCreateRequest;
import woohakdong.server.api.controller.group.dto.GroupIdResponse;
import woohakdong.server.api.controller.group.dto.GroupInfoResponse;
import woohakdong.server.api.controller.group.dto.GroupSummaryResponse;
import woohakdong.server.api.service.bank.BankService;
import woohakdong.server.api.service.club.ClubService;

@RestController
@RequestMapping("/v1/clubs")
@RequiredArgsConstructor
public class ClubController implements ClubControllerDocs {

    private final ClubService clubService;
    private final BankService bankService;

    @PostMapping("/validate")
    public void validateClubName(@RequestBody ClubNameValidateRequest request) {
        clubService.validateClubWithNames(request.clubName(), request.clubEnglishName());
    }

    @PostMapping
    public ClubIdResponse createClub(@RequestBody ClubCreateRequest request) {
        return clubService.registerClub(request, LocalDate.now());
    }

    @GetMapping("/{clubId}")
    public ClubInfoResponse getClubInfo(@PathVariable Long clubId) {
        return clubService.findClubInfo(clubId);
    }

    @PutMapping("/{clubId}")
    public ClubInfoResponse updateClubInfo(@PathVariable Long clubId, @RequestBody ClubUpdateRequest request) {
        return clubService.updateClubInfo(clubId, request);
    }

    @GetMapping("/{clubId}/accounts")
    public ClubAccountResponse getClubAccount(@PathVariable Long clubId) {
        return clubService.getClubAccount(clubId);
    }

    @PostMapping("/{clubId}/accounts")
    public void registerClubAccount(@PathVariable Long clubId, @RequestBody ClubAccountRegisterRequest request) {
        clubService.registerClubAccount(clubId, request, LocalDate.now());
    }

    @PostMapping("/accounts/validate")
    public ClubAccountValidateResponse validateClubAccount(@RequestBody ClubAccountValidateRequest request) {
        return bankService.certifyAccount(request);
    }

    @GetMapping("/{clubId}/join")
    public GroupSummaryResponse getClubJoinInfo(@PathVariable Long clubId) {
        return clubService.getClubJoinInfo(clubId);
    }

    @GetMapping
    public ListWrapperResponse<ClubInfoResponse> getJoinedClubs() {
        return ListWrapperResponse.of(clubService.getJoinedClubInfos());
    }

    @GetMapping("/search")
    public ClubSummaryResponse getClubInfoByEnglishName(@RequestParam String clubEnglishName) {
        return clubService.findClubInfoWithEnglishName(clubEnglishName);
    }

    @GetMapping("/{clubId}/terms")
    public ListWrapperResponse<ClubHistoryTermResponse> getClubHistory(@PathVariable Long clubId) {
        return ListWrapperResponse.of(clubService.getClubHistory(clubId));
    }

    @GetMapping("/{clubId}/availability")
    public void checkClubExpired(@PathVariable Long clubId) {
        clubService.checkClubExpired(clubId, LocalDate.now());
    }

    @GetMapping("/{clubId}/payment-group")
    public GroupSummaryResponse getClubPaymentGroupInfo(@PathVariable Long clubId) {
        return clubService.getGroupPaymentInfo(clubId);
    }

    @PostMapping("/{clubId}/groups")
    public GroupIdResponse createClubGroup(@PathVariable Long clubId,
                                           @Valid @RequestBody GroupCreateRequest groupCreateRequest) {
        return null;
    }

    @GetMapping("/{clubId}/groups")
    public ListWrapperResponse<GroupInfoResponse> getClubGroupList(@PathVariable Long clubId) {
        return null;
    }
}
