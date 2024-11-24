package woohakdong.server.api.service.admin.club;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import woohakdong.server.api.controller.admin.club.dto.ClubMemberResponse;
import woohakdong.server.api.controller.admin.club.dto.ClubStartDateResponse;
import woohakdong.server.api.controller.admin.overall.dto.CountResponse;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.club.ClubRepository;
import woohakdong.server.domain.clubHistory.ClubHistory;
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

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static woohakdong.server.domain.clubmember.ClubMemberRole.MEMBER;


@ActiveProfiles("test")
@SpringBootTest
@Transactional
class AdminClubServiceTest {

    @Autowired
    private AdminClubService adminClubService;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private ClubMemberRepository clubMemberRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private MemberRepository memberRepository;

    private School school;
    private Club club;

    @BeforeEach
    void setup() {
        // 초기 데이터 설정
        school = createSchool("Test School", "test.edu");
        club = createClub("Club A", "English Club A");
        Member member1 = createMember("Member 1", "member1@test.edu", school);
        Member member2 = createMember("Member 2", "member2@test.deu", school);
        createItem(club, "Item 1", "photo1.png");
        createItem(club, "Item 2", "photo2.png");
        createClubMember(club, member1, MEMBER, LocalDate.now());
        createClubMember(club, member2, MEMBER, LocalDate.now());
    }

    @Test
    @DisplayName("동아리별 회원 리스트 조회 테스트")
    void getClubMembers() {
        // When
        List<ClubMemberResponse> members = adminClubService.getClubMembers(club.getClubId(), null);

        // Then
        assertThat(members).hasSize(2)
                .extracting("memberName")
                .containsExactlyInAnyOrder("Member 1", "Member 2");
    }

    @Test
    @DisplayName("동아리별 물품 개수 조회 테스트")
    void getClubItemCount() {
        // When
        CountResponse countResponse = adminClubService.getClubItemCount(club.getClubId(), null);

        // Then
        assertThat(countResponse.count()).isEqualTo(2L);
    }

    @Test
    @DisplayName("동아리 운영 기간 조회 테스트")
    void getClubOperationPeriod() {
        // When
        ClubStartDateResponse response = adminClubService.getClubOperationPeriod(club.getClubId());

        // Then
        assertThat(response.startDate()).isEqualTo(club.getCreatedAt());
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