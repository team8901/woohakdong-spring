package woohakdong.server.api.service.bank;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import woohakdong.server.api.controller.club.dto.ClubAccountValidateRequest;
import woohakdong.server.api.controller.club.dto.ClubAccountValidateResponse;
import woohakdong.server.common.exception.CustomException;
import woohakdong.server.domain.admin.adminAccount.AccountType;
import woohakdong.server.domain.admin.adminAccount.AdminAccount;
import woohakdong.server.domain.admin.adminAccount.AdminAccountRepository;
import woohakdong.server.domain.admin.adminAccountHistory.AdminAccountHistory;
import woohakdong.server.domain.admin.adminAccountHistory.AdminAccountHistoryRepository;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.club.ClubRepository;
import woohakdong.server.domain.clubAccount.ClubAccount;
import woohakdong.server.domain.clubAccount.ClubAccountRepository;
import woohakdong.server.domain.clubAccountHistory.ClubAccountHistory;
import woohakdong.server.domain.member.Member;
import woohakdong.server.domain.member.MemberRepository;

import static woohakdong.server.common.exception.CustomErrorInfo.*;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MockBankService implements BankService {

    private final static Map<String, Map<String, String>> certifiedBanks = new HashMap<>();
    private final ClubRepository clubRepository;
    private final ClubAccountRepository clubAccountRepository;
    private final MemberRepository memberRepository;
    private final AdminAccountRepository adminAccountRepository;
    private final AdminAccountHistoryRepository adminAccountHistoryRepository;
    private final ObjectMapper objectMapper;

    @Value("${nh.iscd}")
    private String iscd;

    @Value("${nh.accessToken}")
    private String accessToken;

    private static final String NH_OTHER_BANK_TRANSFER_API_URL = "https://developers.nonghyup.com/ReceivedTransferOtherBank.nh";
    private static final String NH_TRANSFER_API_URL = "https://developers.nonghyup.com/ReceivedTransferAccountNumber.nh";
    private static final String NH_TRANSACTIONS_API_URL = "https://developers.nonghyup.com/InquireTransactionHistory.nh";

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

    @Transactional
    public void transferClubFee(Long memberId, Long clubId) {
        // clubId로 동아리 정보 조회
        Club club = clubRepository.getById(clubId);

        // clubId로 동아리 계좌 정보 조회
        ClubAccount clubAccount = clubAccountRepository.getByClub(club);

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
        header.put("Tsymd", transferDate); // 거래일자
        header.put("Trtm", transferTime);  // 거래시간
        header.put("Iscd", iscd);          // 환경변수로 설정된 ISCD
        header.put("FintechApsno", "001");
        header.put("ApiSvcCd", "ReceivedTransferA");
        header.put("IsTuno", generateUniqueTransferId()); // 무작위 생성된 고유번호
        header.put("AccessToken", accessToken);           // 환경변수로 설정된 AccessToken

        // 은행에 따라 헤더 및 API 설정 다르게 처리
        if (clubAccount.getClubAccountBankCode().equals("011")) {  // 농협은행일 경우
            header.put("ApiNm", "ReceivedTransferAccountNumber");
        } else {  // 그 외 은행일 경우
            header.put("ApiNm", "ReceivedTransferOtherBank");
        }

        // Body 설정
        request.put("Header", header);
        request.put("Bncd", clubAccount.getClubAccountBankCode());  // 은행코드
        request.put("Acno", clubAccount.getClubAccountNumber());    // 계좌번호
        request.put("Tram", club.getClubDues().toString());         // 이체 금액 (동아리 회비)
        request.put("DractOtlt", club.getClubName() + " 회비 이체"); // 출금 메모 (동아리 이름)
        request.put("MractOtlt", member.getMemberName() + "의 회비");  // 입금 메모 (회원 이름)

        // HTTP 요청 보내기
        if (clubAccount.getClubAccountBankCode().equals("011")) {
            sendNHBankTransferRequest(request);
        }
        else {
            sendOtherBankTransferRequest(request);
        }


        // adminAccount Id
        Long adminAccountId = 1L;

        // 1. AdminAccount 조회
        AdminAccount adminAccount = adminAccountRepository.getById(adminAccountId);

        // 2. 잔액 업데이트 (입금/출금에 따라 다르게 처리)
        Long updatedBalance = adminAccount.getAdminAccountAmount();
        updatedBalance -= club.getClubDues();

        // 3. AdminAccountHistory 기록 생성 및 저장
        AdminAccountHistory history = AdminAccountHistory.builder()
                .adminAccountHistoryInOutType(AccountType.WITHDRAW) // 출금
                .adminAccountHistoryTranDate(LocalDate.now())
                .adminAccountHistoryBalanceAmount(updatedBalance) // 업데이트된 잔액
                .adminAccountHistoryTranAmount(Long.valueOf(club.getClubDues()))    // 이체된 금액
                .adminAccountHistoryContent(club.getClubName() + " 회비 이체 " + member.getMemberName() + "의 회비")           // 이체 내역 설명
                .adminAccount(adminAccount)                    // 연관된 AdminAccount 설정
                .build();

        adminAccountHistoryRepository.save(history);

        // 4. AdminAccount의 잔액 업데이트 후 저장
        adminAccount.setAdminAccountAmount(updatedBalance);  // AdminAccount의 잔액 설정
        adminAccountRepository.save(adminAccount);  // 업데이트된 계좌 정보 저장
    }

    @Transactional
    public List<ClubAccountHistory> fetchTransactions(ClubAccount clubAccount, LocalDate startDate, LocalDate endDate) {
        // 이체 요청 JSON 데이터 생성
        Map<String, Object> request = new HashMap<>();
        Map<String, String> header = new HashMap<>();

        LocalDateTime now = LocalDateTime.now();
        String transferDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String transferTime = now.format(DateTimeFormatter.ofPattern("HHmmss"));

        // Header 설정
        header.put("Tsymd", transferDate); // 거래일자
        header.put("Trtm", transferTime);  // 거래시간
        header.put("Iscd", iscd);          // 환경변수로 설정된 ISCD
        header.put("FintechApsno", "001");
        header.put("ApiSvcCd", "ReceivedTransferA");
        header.put("IsTuno", generateUniqueTransferId()); // 무작위 생성된 고유번호
        header.put("AccessToken", accessToken);           // 환경변수로 설정된 AccessToken
        header.put("ApiNm", "InquireTransactionHistory");

        String sDate = startDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String eDate = endDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // Body 설정
        request.put("Header", header);
        request.put("Bncd", clubAccount.getClubAccountBankCode());  // 은행코드
        request.put("Acno", clubAccount.getClubAccountNumber());    // 계좌번호
        request.put("Insymd", sDate);
        request.put("Ineymd", eDate);
        request.put("TrnsDsnc", "A");
        request.put("Lnsq", "DESC");
        request.put("PageNo", "1");
        request.put("Dmcnt", "100");

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                NH_TRANSACTIONS_API_URL,
                HttpMethod.POST,
                entity,
                Map.class
        );

        Map<String, Object> responseBody = response.getBody();
        if (responseBody == null || !responseBody.containsKey("Header")) {
            throw new CustomException(GET_TRANSACTION_FAILED); // 예외 처리
        }

        Map<String, Object> responseHeader = (Map<String, Object>) responseBody.get("Header");
        if (!"00000".equals(responseHeader.get("Rpcd"))) {
            throw new CustomException(GET_TRANSACTION_FAILED); // 예외 처리
        }

        NHTransactionResponse nhResponse = objectMapper.convertValue(responseBody, NHTransactionResponse.class);
        if (nhResponse == null || nhResponse.getREC() == null) {
            return Collections.emptyList(); // null인 경우 빈 리스트 반환
        }

        LocalDateTime lastUpdateDate = clubAccount.getClubAccountLastUpdateDate();

        List<ClubAccountHistory> histories = new ArrayList<>();

        // NH 응답 데이터를 순회하면서 필요한 조건에 맞는 ClubAccountHistory 객체를 생성하고 추가
        for (NHTransactionResponse.Record record : nhResponse.getREC()) {
            // Trdd와 Txtm을 합쳐 LocalDateTime으로 변환
            LocalDateTime transactionDateTime = LocalDateTime.parse(
                    record.getTrdd() + "T" + record.getTxtm(),
                    DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss")
            );

            // lastUpdateDate 이후의 거래만 추가
            if (transactionDateTime.isAfter(lastUpdateDate)) {
                ClubAccountHistory history = ClubAccountHistory.builder()
                        .clubAccountHistoryInOutType(
                                record.getTrnsAfAcntBlncSmblCd().equals("+") ? AccountType.DEPOSIT : AccountType.WITHDRAW)
                        .clubAccountHistoryTranDate(transactionDateTime)
                        .clubAccountHistoryBalanceAmount(Long.parseLong(record.getAftrBlnc()))
                        .clubAccountHistoryTranAmount(Long.parseLong(record.getTram()))
                        .clubAccountHistoryContent(record.getBnprCntn())
                        .clubAccount(clubAccount)
                        .build();

                histories.add(history);
            }
        }

        return histories;
    }

    private void sendOtherBankTransferRequest(Map<String, Object> request) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                NH_OTHER_BANK_TRANSFER_API_URL,
                HttpMethod.POST,
                entity,
                Map.class
        );

        // 응답 처리
        Map<String, Object> responseBody = response.getBody();
        if (responseBody != null) {
            Map<String, String> responseHeader = (Map<String, String>) responseBody.get("Header");
            if (!"00000".equals(responseHeader.get("Rpcd"))) {
                System.out.println(responseHeader.get("Rsms"));
                throw new CustomException(TRANSFER_FAILED);
            } else {
                System.out.println("이체 성공: " + responseHeader.get("Rsms"));
            }
        }
    }

    private void sendNHBankTransferRequest(Map<String, Object> request) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                NH_TRANSFER_API_URL,
                HttpMethod.POST,
                entity,
                Map.class
        );

        // 응답 처리
        Map<String, Object> responseBody = response.getBody();
        if (responseBody != null) {
            Map<String, String> responseHeader = (Map<String, String>) responseBody.get("Header");
            if (!"00000".equals(responseHeader.get("Rpcd"))) {
                System.out.println(responseHeader.get("Rsms"));
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
