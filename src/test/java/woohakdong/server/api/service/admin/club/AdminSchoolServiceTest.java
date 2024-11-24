package woohakdong.server.api.service.admin.club;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import woohakdong.server.api.controller.admin.overall.dto.ClubListResponse;
import woohakdong.server.api.controller.admin.overall.dto.CountResponse;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.club.ClubRepository;
import woohakdong.server.domain.clubHistory.ClubHistory;
import woohakdong.server.domain.clubHistory.ClubHistoryRepository;
import woohakdong.server.domain.clubmember.ClubMember;
import woohakdong.server.domain.clubmember.ClubMemberRepository;
import woohakdong.server.domain.clubmember.ClubMemberRole;
import woohakdong.server.domain.item.Item;
import woohakdong.server.domain.item.ItemCategory;
import woohakdong.server.domain.item.ItemRepository;
import woohakdong.server.domain.member.Member;
import woohakdong.server.domain.member.MemberRepository;
import woohakdong.server.domain.school.School;
import woohakdong.server.domain.school.SchoolRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static woohakdong.server.domain.clubmember.ClubMemberRole.MEMBER;


@ActiveProfiles("test")
@SpringBootTest
@Transactional
class AdminSchoolServiceTest {

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private AdminSchoolService adminSchoolService;

    @Autowired
    private ClubMemberRepository clubMemberRepository;

    @Autowired
    private ClubHistoryRepository clubHistoryRepository;

    private School school;

    @BeforeEach
    void setup() {
        // 초기 데이터 설정
        school = createSchool("Test School", "test.edu");
        Club club1 = createClub("Club A", "English Club A");
        Club club2 = createClub("Club B", "English Club B");
        Member member1 = createMember("Member 1", "member1@test.edu", school);
        Member member2 = createMember("Member 2", "member2@test.deu", school);
        createItem(club1, "Item 1", "photo1.png");
        createItem(club2, "Item 2", "photo2.png");
        createClubMember(club1, member1, MEMBER, LocalDate.now());
        createClubMember(club1, member2, MEMBER, LocalDate.now());
        createClubMember(club2, member1, MEMBER, LocalDate.now());
        createClubHistory(club1, LocalDate.of(2024, 7,1));
        createClubHistory(club2, LocalDate.of(2024, 7,1));
    }

    @Test
    @DisplayName("학교별 등록된 동아리 수 반환 테스트")
    void getClubCountBySchool() {
        // when
        CountResponse response = adminSchoolService.getClubCountBySchool(school.getSchoolId(), null);

        // then
        assertThat(response).extracting("count").isEqualTo(2L);
    }

    @Test
    @DisplayName("학교별 가입된 회원 수 반환 테스트")
    void getMemberCountBySchool() {
        // when
        CountResponse response = adminSchoolService.getMemberCountBySchool(school.getSchoolId(), null);

        // then
        assertThat(response).extracting("count").isEqualTo(3L);
    }

    @Test
    @DisplayName("학교별 등록된 물품 수 반환 테스트")
    void getItemCountBySchool() {
        // when
        CountResponse response = adminSchoolService.getItemCountBySchool(school.getSchoolId(), null);

        // then
        assertThat(response).extracting("count").isEqualTo(2L);
    }

    @Test
    @DisplayName("학교별 등록된 동아리 리스트 반환 테스트")
    void getClubListBySchool() {
        // when
        List<ClubListResponse> response = adminSchoolService.getClubListBySchool(school.getSchoolId(), null);

        // then
        assertThat(response).hasSize(2)
                .extracting("clubName", "schoolName")
                .containsExactlyInAnyOrder(
                        tuple("Club A", "Test School"),
                        tuple("Club B", "Test School")
                );
    }

    @Test
    @DisplayName("학교별 분기별 등록된 동아리 수 반환 테스트")
    void getClubCountBySchoolAndTerm() {
        // when
        CountResponse response = adminSchoolService.getClubCountBySchool(school.getSchoolId(), LocalDate.of(2024, 7,1));

        // then
        assertThat(response).extracting("count").isEqualTo(2L);
    }

    @Test
    @DisplayName("학교별 분기별 가입된 회원 수 반환 테스트")
    void getMemberCountBySchoolAndTerm() {
        // when
        CountResponse response = adminSchoolService.getMemberCountBySchool(school.getSchoolId(), LocalDate.of(2024, 7,1));

        // then
        assertThat(response).extracting("count").isEqualTo(3L);
    }

    @Test
    @DisplayName("학교별 분기별 등록된 물품 수 반환 테스트")
    void getItemCountBySchoolAndTerm() {
        // when
        CountResponse response = adminSchoolService.getItemCountBySchool(school.getSchoolId(), LocalDate.of(2024, 7,1));

        // then
        assertThat(response).extracting("count").isEqualTo(2L);
    }

    @Test
    @DisplayName("학교별 분기별 등록된 동아리 리스트 반환 테스트")
    void getClubListBySchoolAndTerm() {
        // when
        List<ClubListResponse> response = adminSchoolService.getClubListBySchool(school.getSchoolId(), LocalDate.of(2024, 7,1));

        // then
        assertThat(response).hasSize(2)
                .extracting("clubName", "schoolName")
                .containsExactlyInAnyOrder(
                        tuple("Club A", "Test School"),
                        tuple("Club B", "Test School")
                );
    }

    private Item createItem(Club club1, String name, String image) {
        Item item = Item.builder()
                .club(club1)
                .itemName(name)
                .itemPhoto(image)
                .itemAvailable(true)
                .itemUsing(false)
                .itemCategory(ItemCategory.BOOK)
                .itemRentalMaxDay(7)
                .build();
        return itemRepository.save(item);
    }

    private Member createMember(String name, String email, School school) {
        Member member = Member.builder()
                .memberProvideId("provide_test")
                .memberName(name)
                .memberEmail(email)
                .school(school)
                .build();
        return memberRepository.save(member);
    }

    private Club createClub(String name, String englishName) {
        Club club = Club.builder()
                .clubName(name)
                .clubEnglishName(englishName)
                .clubGroupChatLink("qwer")
                .school(school)
                .clubExpirationDate(LocalDate.of(2024, 11, 19))
                .build();
        return clubRepository.save(club);
    }

    private School createSchool(String name, String domain) {
        School school = School.builder()
                .schoolName(name)
                .schoolDomain(domain)
                .build();
        return schoolRepository.save(school);
    }

    private ClubHistory createClubHistory(Club club, LocalDate date) {
        ClubHistory clubHistory = ClubHistory.create(club, date);
        return clubHistoryRepository.save(clubHistory);
    }

    private ClubMember createClubMember(Club club, Member member, ClubMemberRole memberRole, LocalDate assignedTerm) {
        ClubMember clubMember = ClubMember.builder()
                .clubMemberAssignedTerm(getAssignedTerm(assignedTerm))
                .club(club)
                .member(member)
                .clubMemberRole(memberRole)
                .build();
        return clubMemberRepository.save(clubMember);
    }

    private LocalDate getAssignedTerm(LocalDate date) {
        int year = date.getYear();
        int semester = date.getMonthValue() <= 6 ? 1 : 7; // 1: 1학기, 7: 2학기
        return LocalDate.of(year, semester, 1);
    }
}