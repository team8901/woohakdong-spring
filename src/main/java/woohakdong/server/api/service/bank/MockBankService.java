package woohakdong.server.api.service.bank;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import woohakdong.server.api.controller.club.dto.ClubAccountValidateRequest;
import woohakdong.server.api.controller.club.dto.ClubAccountValidateResponse;
import woohakdong.server.common.exception.CustomException;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.club.ClubRepository;
import woohakdong.server.domain.clubAccount.ClubAccount;
import woohakdong.server.domain.clubAccount.ClubAccountRepository;
import woohakdong.server.domain.member.Member;
import woohakdong.server.domain.member.MemberRepository;

import static woohakdong.server.common.exception.CustomErrorInfo.*;

@RequiredArgsConstructor
@Service
public class MockBankService implements BankService {

    private final static Map<String, Map<String, String>> certifiedBanks = new HashMap<>();
    private final ClubRepository clubRepository;
    private final ClubAccountRepository clubAccountRepository;
    private final MemberRepository memberRepository;

    @Value("${nh.iscd}")
    private String iscd;

    @Value("${nh.accessToken}")
    private String accessToken;

    private static final String NH_API_URL = "https://developers.nonghyup.com/ReceivedTransferOtherBank.nh";

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

    public void transferClubFee(Long memberId, Long clubId) {
        // clubId로 동아리 정보 조회
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new CustomException(CLUB_NOT_FOUND));

        // clubId로 동아리 계좌 정보 조회
        ClubAccount clubAccount = clubAccountRepository.findByClub(club)
                .orElseThrow(() -> new CustomException(BANK_INVALID_ACCOUNT_NUMBER));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        // 현재 날짜와 시간 구하기 (형식: yyyyMMdd HHmmss)
        LocalDateTime now = LocalDateTime.now();
        String transferDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String transferTime = now.format(DateTimeFormatter.ofPattern("HHmmss"));

        // 이체 요청 JSON 데이터 생성
        Map<String, Object> request = new HashMap<>();
        Map<String, String> header = new HashMap<>();

        // Header 설정
        header.put("ApiNm", "ReceivedTransferOtherBank");
        header.put("Tsymd", transferDate); // 거래일자
        header.put("Trtm", transferTime);  // 거래시간
        header.put("Iscd", iscd);          // 환경변수로 설정된 ISCD
        header.put("FintechApsno", "001");
        header.put("ApiSvcCd", "ReceivedTransferA");
        header.put("IsTuno", generateUniqueTransferId()); // 무작위 생성된 고유번호
        header.put("AccessToken", accessToken);           // 환경변수로 설정된 AccessToken

        // Body 설정
        request.put("Header", header);
        request.put("Bncd", clubAccount.getClubAccountBankCode());  // 은행코드
        request.put("Acno", clubAccount.getClubAccountNumber());    // 계좌번호
        request.put("Tram", club.getClubDues().toString());         // 이체 금액 (동아리 회비)
        request.put("DractOtlt", club.getClubName() + " 회비 이체"); // 출금 메모 (동아리 이름)
        request.put("MractOtlt", member.getMemberName() + "의 회비");  // 입금 메모 (회원 이름)

        // HTTP 요청 보내기
        sendTransferRequest(request);
    }

    private void sendTransferRequest(Map<String, Object> request) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                NH_API_URL,
                HttpMethod.POST,
                entity,
                Map.class
        );

        // 응답 처리
        Map<String, Object> responseBody = response.getBody();
        if (responseBody != null) {
            Map<String, String> responseHeader = (Map<String, String>) responseBody.get("Header");
            if (!"00000".equals(responseHeader.get("Rpcd"))) {
                throw new CustomException(TRANSFER_FAILED);
            } else {
                System.out.println("이체 성공: " + responseHeader.get("Rsms"));
            }
        }
    }

    private String generateUniqueTransferId() {
        // 고유한 이체 고유번호 생성 로직 (예: UUID 사용)
        return UUID.randomUUID().toString().replace("-", "").substring(0, 20);
    }
}
