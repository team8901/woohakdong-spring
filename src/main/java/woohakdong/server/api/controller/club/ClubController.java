package woohakdong.server.api.controller.club;

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
import woohakdong.server.api.controller.club.dto.*;
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
    public ClubCreateResponse createClub(@RequestBody ClubCreateRequest request) {
        return clubService.registerClub(request);
    }

    @GetMapping("/{clubId}")
    public ClubInfoResponse getClubInfo(@PathVariable Long clubId) {
        return clubService.findClubInfo(clubId);
    }

    @PutMapping("/{clubId}")
    public ClubInfoResponse updateClubInfo(@PathVariable Long clubId, @RequestBody ClubUpdateRequest request) {
        return clubService.updateClubInfo(clubId, request);
    }

    @PostMapping("/{clubId}/accounts")
    public void registerClubAccount(@PathVariable Long clubId, @RequestBody ClubAccountRegisterRequest request) {
        clubService.registerClubAccount(clubId, request);
    }

    @PostMapping("/accounts/validate")
    public ClubAccountValidateResponse validateClubAccount(@RequestBody ClubAccountValidateRequest request) {
        return bankService.certifyAccount(request);
    }

    @GetMapping("/{clubId}/join")
    public ClubJoinGroupInfoResponse getClubJoinInfo(@PathVariable Long clubId) {
        return clubService.getClubJoinInfo(clubId);
    }

    @GetMapping
    public ListWrapperResponse<ClubInfoResponse> getJoinedClubs() {
        return ListWrapperResponse.of(clubService.getJoinedClubInfos());
    }

    @GetMapping("/search")
    public ClubInfoResponse getClubInfoByEnglishName(@RequestParam String clubEnglishName) {
        return clubService.findClubInfoWithEnglishName(clubEnglishName);
    }

    @GetMapping("/{clubId}/terms")
    public ListWrapperResponse<ClubHistoryTermResponse> getClubHistory(@PathVariable Long clubId) {
        return clubService.getClubHistory(clubId);
    }
}
