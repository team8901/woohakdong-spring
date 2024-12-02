package woohakdong.server.domain.clubAccount;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import woohakdong.server.RepositoryTestSetup;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.club.ClubRepository;

class ClubAccountRepositoryTest extends RepositoryTestSetup {

    @Autowired
    private ClubAccountRepository clubAccountRepository;

    @Autowired
    private ClubRepository clubRepository;

    @BeforeEach
    void setUp() {
        club = createClub();
    }

    private Club club;

    @DisplayName("은행 이름과 계좌 번호로 계좌 존재 여부를 확인한다.")
    @ParameterizedTest(name = "{index} => bankName={0}, accountNumber={1}, expectedResult={2}")
    @CsvSource({
            "테스트은행, 1111111111, true",
            "테스트은행, 0000000000, false",
            "nonexistent, 1111111111, false",
            "nonexistent, 0000001111, false"
    })
    void existsByClubAccountBankNameAndClubAccountNumber(String bankName, String accountNumber,
                                                         boolean expectedResult) {
        // Given
        createClubAccount();

        // When
        boolean result = clubAccountRepository.existsByClubAccountBankNameAndClubAccountNumber(bankName, accountNumber);

        // Then
        assertThat(result).isEqualTo(expectedResult);
    }


    @DisplayName("동아리 정보로 계좌 정보를 조회한다.")
    @Test
    void test() {
        // Given
        createClubAccount();

        // When
        ClubAccount savedClubAccount = clubAccountRepository.getByClub(club);

        // Then
        assertThat(savedClubAccount)
                .extracting("clubAccountBankName", "clubAccountPinTechNumber")
                .containsExactly("테스트은행", "PIN-11111111");
    }

    private Club createClub() {
        Club club = Club.builder()
                .clubName("테스트동아리")
                .clubEnglishName("testClub")
                .clubGroupChatLink("https://club-group-chat-link.com")
                .clubExpirationDate(LocalDate.of(2024, 11, 19))
                .build();
        return clubRepository.save(club);
    }

    private ClubAccount createClubAccount() {
        ClubAccount clubAccount = ClubAccount.builder()
                .clubAccountBankName("테스트은행")
                .clubAccountNumber("1111111111")
                .clubAccountPinTechNumber("PIN-11111111")
                .clubAccountBankCode("011")
                .club(club)
                .build();
        return clubAccountRepository.save(clubAccount);
    }
}