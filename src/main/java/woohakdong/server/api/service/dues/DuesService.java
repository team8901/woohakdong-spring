package woohakdong.server.api.service.dues;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woohakdong.server.api.controller.dues.dto.ClubAccountHistoryListResponse;
import woohakdong.server.api.service.bank.MockBankService;
import woohakdong.server.common.exception.CustomException;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.club.ClubRepository;
import woohakdong.server.domain.clubAccount.ClubAccount;
import woohakdong.server.domain.clubAccount.ClubAccountRepository;
import woohakdong.server.domain.clubAccountHistory.ClubAccountHistory;
import woohakdong.server.domain.clubAccountHistory.ClubAccountHistoryRepository;
import woohakdong.server.domain.member.MemberRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static woohakdong.server.common.exception.CustomErrorInfo.CLUB_ACCOUNT_NOT_FOUND;
import static woohakdong.server.common.exception.CustomErrorInfo.CLUB_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DuesService {

    private final ClubAccountHistoryRepository clubAccountHistoryRepository;
    private final ClubRepository clubRepository;
    private final ClubAccountRepository clubAccountRepository;
    private final MockBankService mockBankService;

    public void fetchAndSaveRecentTransactions(Long clubId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new CustomException(CLUB_NOT_FOUND));

        ClubAccount clubAccount = clubAccountRepository.findByClub(club)
                .orElseThrow(() -> new CustomException(CLUB_ACCOUNT_NOT_FOUND));

        LocalDateTime lastUpdateDate = clubAccount.getClubAccountLastUpdateDate() != null
                ? clubAccount.getClubAccountLastUpdateDate()
                : LocalDateTime.of(2000, 1, 1, 0, 0); // 기본값

        // 오늘 날짜를 기준으로 거래 내역을 가져옴
        List<ClubAccountHistory> histories = mockBankService.fetchTransactions(clubAccount, lastUpdateDate.toLocalDate(), LocalDate.now());

        if (!histories.isEmpty()) {
            clubAccountHistoryRepository.saveAll(histories);

            clubAccount.setClubAccountLastUpdateDate(LocalDateTime.now());
            clubAccountRepository.save(clubAccount);
        }
    }

    public List<ClubAccountHistoryListResponse> getMonthlyTransactions(Long clubId, int year, int month) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new CustomException(CLUB_NOT_FOUND));

        ClubAccount clubAccount = clubAccountRepository.findByClub(club)
                .orElseThrow(() -> new CustomException(CLUB_ACCOUNT_NOT_FOUND));

        List<ClubAccountHistory> histories = clubAccountHistoryRepository.findMonthlyTransactions(
                clubAccount, year, month);

        return histories.stream()
                .map(history -> new ClubAccountHistoryListResponse(
                        history.getClubAccountHistoryId(),
                        history.getClubAccountHistoryInOutType(),
                        history.getClubAccountHistoryTranDate(),
                        history.getClubAccountHistoryBalanceAmount(),
                        history.getClubAccountHistoryTranAmount(),
                        history.getClubAccountHistoryContent()
                ))
                .collect(Collectors.toList());
    }
}