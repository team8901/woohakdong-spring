package woohakdong.server.api.controller.club;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import woohakdong.server.api.controller.club.dto.ClubAccountRegisterRequest;
import woohakdong.server.api.controller.club.dto.ClubAccountValidateRequest;
import woohakdong.server.api.controller.club.dto.ClubAccountValidateResponse;
import woohakdong.server.api.controller.club.dto.ClubCreateRequest;
import woohakdong.server.api.controller.club.dto.ClubCreateResponse;
import woohakdong.server.api.controller.club.dto.ClubInfoResponse;
import woohakdong.server.api.controller.club.dto.ClubJoinGatheringInfoResponse;
import woohakdong.server.api.service.bank.BankService;
import woohakdong.server.api.service.club.ClubService;

@RestController
@RequestMapping("/v1/clubs")
@RequiredArgsConstructor
public class ClubController implements ClubControllerDocs {

    private final ClubService clubService;
    private final BankService bankService;

    @PostMapping("/validate")
    public void validateClubName(String clubName) {

    }

    @PostMapping
    public ClubCreateResponse createClub(ClubCreateRequest clubCreateRequest) {
        return clubService.registerClub(clubCreateRequest);
    }

    @GetMapping("/{clubId}")
    public ClubInfoResponse getClubInfo(@PathVariable Long clubId) {
        return null;
    }

    @PutMapping("/{clubId}")
    public ClubInfoResponse updateClubInfo(@PathVariable Long clubId, ClubCreateRequest clubCreateRequest) {
        return null;
    }

    @PostMapping("/{clubId}/accounts")
    public void registerClubAccount(@PathVariable Long clubId, ClubAccountRegisterRequest clubAccountRegisterRequest) {
        clubService.registerClubAccount(clubId, clubAccountRegisterRequest);
    }

    @PostMapping("/accounts/validate")
    public ClubAccountValidateResponse validateClubAccount(ClubAccountValidateRequest clubAccountValidateRequest) {
        return bankService.certifyAccount(clubAccountValidateRequest);
    }

    @GetMapping("/{clubId}/join")
    public ClubJoinGatheringInfoResponse getClubJoinInfo(@PathVariable Long clubId) {
        return null;
    }
}
