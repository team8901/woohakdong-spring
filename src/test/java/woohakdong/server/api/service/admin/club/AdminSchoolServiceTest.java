package woohakdong.server.api.service.admin.club;

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
import woohakdong.server.domain.item.Item;
import woohakdong.server.domain.item.ItemCategory;
import woohakdong.server.domain.item.ItemRepository;
import woohakdong.server.domain.member.Member;
import woohakdong.server.domain.member.MemberRepository;
import woohakdong.server.domain.school.School;
import woohakdong.server.domain.school.SchoolRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


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

    private School school;

    @BeforeEach
    void setup() {
        // 초기 데이터 설정
        school = schoolRepository.save(School.builder()
                .schoolName("Test School")
                .schoolDomain("test.edu")
                .build());

        Club club1 = Club.builder()
                .clubName("Club A")
                .clubEnglishName("English Club A")
                .clubGroupChatLink("qwer")
                .school(school)
                .build();

        Club club2 = Club.builder()
                .clubName("Club B")
                .clubEnglishName("English Club B")
                .clubGroupChatLink("qwer")
                .school(school)
                .build();

        clubRepository.save(club1);
        clubRepository.save(club2);

        Member member1 = Member.builder()
                .memberProvideId("id1")
                .memberName("Member 1")
                .memberEmail("member1@test.edu")
                .school(school)
                .build();

        Member member2 = Member.builder()
                .memberProvideId("id2")
                .memberName("Member 2")
                .memberEmail("member2@test.edu")
                .school(school)
                .build();

        memberRepository.save(member1);
        memberRepository.save(member2);

        Item item1 = Item.builder()
                .club(club1)
                .itemName("Item 1")
                .itemPhoto("photo1.png")
                .itemAvailable(true)
                .itemUsing(false)
                .itemCategory(ItemCategory.BOOK)
                .itemRentalMaxDay(7)
                .build();

        Item item2 = Item.builder()
                .club(club2)
                .itemName("Item 2")
                .itemPhoto("photo2.png")
                .itemAvailable(true)
                .itemUsing(false)
                .itemCategory(ItemCategory.BOOK)
                .itemRentalMaxDay(7)
                .build();

        itemRepository.save(item1);
        itemRepository.save(item2);
    }

    @Test
    @DisplayName("학교별 등록된 동아리 수 반환 테스트")
    void getClubCountBySchool() {
        // when
        CountResponse response = adminSchoolService.getClubCountBySchool(school.getSchoolId());

        // then
        assertThat(response).extracting("count").isEqualTo(2L);
    }

    @Test
    @DisplayName("학교별 가입된 회원 수 반환 테스트")
    void getMemberCountBySchool() {
        // when
        CountResponse response = adminSchoolService.getMemberCountBySchool(school.getSchoolId());

        // then
        assertThat(response).extracting("count").isEqualTo(2L);
    }

    @Test
    @DisplayName("학교별 등록된 물품 수 반환 테스트")
    void getItemCountBySchool() {
        // when
        CountResponse response = adminSchoolService.getItemCountBySchool(school.getSchoolId());

        // then
        assertThat(response).extracting("count").isEqualTo(2L);
    }

    @Test
    @DisplayName("학교별 등록된 동아리 리스트 반환 테스트")
    void getClubListBySchool() {
        // when
        List<ClubListResponse> response = adminSchoolService.getClubListBySchool(school.getSchoolId());

        // then
        assertThat(response).hasSize(2);
        assertThat(response.get(0)).extracting("clubName", "schoolName")
                .containsExactly("Club A", "Test School");
        assertThat(response.get(1)).extracting("clubName", "schoolName")
                .containsExactly("Club B", "Test School");
    }
}