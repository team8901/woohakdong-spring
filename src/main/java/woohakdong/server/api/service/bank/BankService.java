package woohakdong.server.api.service.bank;

import woohakdong.server.api.controller.club.dto.ClubAccountValidateRequest;
import woohakdong.server.api.controller.club.dto.ClubAccountValidateResponse;

public interface BankService {
    ClubAccountValidateResponse certifyAccount(ClubAccountValidateRequest clubAccountValidateRequest);
}
