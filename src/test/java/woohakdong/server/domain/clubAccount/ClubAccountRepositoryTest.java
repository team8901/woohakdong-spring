package woohakdong.server.domain.clubAccount;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.club.ClubRepository;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class ClubAccountRepositoryTest {

    @Autowired
    private ClubAccountRepository clubAccountRepository;

    @Autowired
    private ClubRepository clubRepository;

    @DisplayName("은행 이름과 계좌 번호로 계좌 존재 여부를 확인한다.")
    @Test
    void existsByClubAccountBankNameAndClubAccountNumber() {
        // Given
        Club club = Club.builder()
                .clubName("테스트동아리")
                .clubEnglishName("testClub")
                .clubGroupChatLink("https://club-group-chat-link.com")
                .build();
        clubRepository.save(club);

        ClubAccount clubAccount = ClubAccount.builder()
                .clubAccountBankName("테스트은행")
                .clubAccountNumber("1111111111")
                .clubAccountPinTechNumber("PIN-11111111")
                .clubAccountBankCode("011")
                .club(club)
                .build();
        clubAccountRepository.save(clubAccount);

        // When
        Boolean result1 = clubAccountRepository.existsByClubAccountBankNameAndClubAccountNumber(
                clubAccount.getClubAccountBankName(), clubAccount.getClubAccountNumber());
        Boolean result2 = clubAccountRepository.existsByClubAccountBankNameAndClubAccountNumber("nonexistent",
                clubAccount.getClubAccountNumber());
        Boolean result3 = clubAccountRepository.existsByClubAccountBankNameAndClubAccountNumber(
                clubAccount.getClubAccountBankName(), "nonexistent");

        // Then
        assertThat(result1).isTrue();
        assertThat(result2).isFalse();
        assertThat(result3).isFalse();
    }

    @DisplayName("동아리 정보로 계좌 정보를 조회한다.")
    @Test
    void test() {
        // Given
        Club club = Club.builder()
                .clubName("테스트동아리")
                .clubEnglishName("testClub")
                .clubGroupChatLink("https://club-group-chat-link.com")
                .build();
        clubRepository.save(club);

        ClubAccount clubAccount = ClubAccount.builder()
                .clubAccountBankName("테스트은행")
                .clubAccountNumber("1111111111")
                .clubAccountPinTechNumber("PIN-11111111")
                .clubAccountBankCode("011")
                .club(club)
                .build();
        clubAccountRepository.save(clubAccount);

        // When
        ClubAccount savedClubAccount = clubAccountRepository.getByClub(club);

        // Then
        assertThat(savedClubAccount)
                .extracting("clubAccountBankName", "clubAccountPinTechNumber")
                .containsExactly("테스트은행", "PIN-11111111");
    }

}