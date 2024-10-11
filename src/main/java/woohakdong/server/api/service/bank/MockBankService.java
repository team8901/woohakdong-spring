package woohakdong.server.api.service.bank;

import static woohakdong.server.common.exception.CustomErrorInfo.BANK_INVALID_ACCOUNT_NUMBER;
import static woohakdong.server.common.exception.CustomErrorInfo.BANK_NOT_SUPPORTED;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import woohakdong.server.api.controller.club.dto.ClubAccountValidateRequest;
import woohakdong.server.api.controller.club.dto.ClubAccountValidateResponse;
import woohakdong.server.common.exception.CustomException;

@Service
public class MockBankService implements BankService {

    private final static Map<String, Map<String, String>> certifiedBanks = new HashMap<>();

    static {
        Map<String, String> kbBank = new HashMap<>();
        kbBank.put("1234567890", "PIN-1234567890"); // 계좌번호, PIN
        kbBank.put("1111111111", "PIN-1111111111");

        Map<String, String> shinhanBank = new HashMap<>();
        shinhanBank.put("2222222222", "PIN-2222222222");
        shinhanBank.put("3333333333", "PIN-3333333333");

        certifiedBanks.put("국민은행", kbBank);
        certifiedBanks.put("신한은행", shinhanBank);
    }

    public ClubAccountValidateResponse certifyAccount(ClubAccountValidateRequest clubAccountValidateRequest) {
        String clubAccountBankName = clubAccountValidateRequest.clubAccountBankName();
        String clubAccountNumber = clubAccountValidateRequest.clubAccountNumber();

        if (!certifiedBanks.containsKey(clubAccountBankName)) {
            throw new CustomException(BANK_NOT_SUPPORTED);
        }

        Map<String, String> bank = certifiedBanks.get(clubAccountBankName);
        if (!bank.containsKey(clubAccountNumber)) {
            throw new CustomException(BANK_INVALID_ACCOUNT_NUMBER);
        }

        return ClubAccountValidateResponse.builder()
                .clubAccountBankName(clubAccountBankName)
                .clubAccountNumber(clubAccountNumber)
                .clubAccountPinTechNumber(bank.get(clubAccountNumber))
                .build();
    }
}
