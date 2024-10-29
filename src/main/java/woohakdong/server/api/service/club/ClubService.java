package woohakdong.server.api.service.club;

import static woohakdong.server.common.exception.CustomErrorInfo.CLUB_MEMBER_ROLE_NOT_ALLOWED;
import static woohakdong.server.common.exception.CustomErrorInfo.CLUB_NAME_DUPLICATION;
import static woohakdong.server.common.exception.CustomErrorInfo.CLUB_NOT_FOUND;
import static woohakdong.server.common.exception.CustomErrorInfo.GROUP_NOT_FOUND;
import static woohakdong.server.common.exception.CustomErrorInfo.INVALID_BANK_NAME;
import static woohakdong.server.common.exception.CustomErrorInfo.MEMBER_NOT_FOUND;
import static woohakdong.server.common.exception.CustomErrorInfo.SCHOOL_NOT_FOUND;
import static woohakdong.server.domain.clubmember.ClubMemberRole.PRESIDENT;
import static woohakdong.server.domain.group.GroupType.JOIN;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woohakdong.server.api.controller.ListWrapperResponse;
import woohakdong.server.api.controller.club.dto.*;
import woohakdong.server.common.exception.CustomException;
import woohakdong.server.common.security.jwt.CustomUserDetails;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.club.ClubRepository;
import woohakdong.server.domain.clubAccount.ClubAccount;
import woohakdong.server.domain.clubAccount.ClubAccountRepository;
import woohakdong.server.domain.clubHistory.ClubHistory;
import woohakdong.server.domain.clubHistory.ClubHistoryRepository;
import woohakdong.server.domain.clubmember.ClubMember;
import woohakdong.server.domain.clubmember.ClubMemberRepository;
import woohakdong.server.domain.clubmember.ClubMemberRole;
import woohakdong.server.domain.group.Group;
import woohakdong.server.domain.group.GroupRepository;
import woohakdong.server.domain.member.Member;
import woohakdong.server.domain.member.MemberRepository;
import woohakdong.server.domain.school.School;
import woohakdong.server.domain.school.SchoolRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ClubService {

    private final ClubRepository clubRepository;
    private final MemberRepository memberRepository;
    private final SchoolRepository schoolRepository;
    private final ClubAccountRepository clubAccountRepository;
    private final ClubMemberRepository clubMemberRepository;
    private final GroupRepository groupRepository;
    private final ClubHistoryRepository clubHistoryRepository;

    public void validateClubWithNames(String clubName, String clubEnglishName) {
        if (clubRepository.existsByClubNameOrClubEnglishName(clubName, clubEnglishName)) {
            throw new CustomException(CLUB_NAME_DUPLICATION);
        }
    }

    @Transactional
    public ClubCreateResponse registerClub(ClubCreateRequest clubCreateRequest) {
        Member member = getMemberFromJwtInformation();
        School school = schoolRepository.findById(member.getSchool().getSchoolId())
                .orElseThrow(() -> new CustomException(SCHOOL_NOT_FOUND));

        Club club = createClub(clubCreateRequest, school);
        club.addGroup(createJoinGroup(club, clubCreateRequest));

        ClubMember clubMember = createClubMember(member, club, PRESIDENT);
        club.addClubMember(clubMember);

        // club history에 이번 분기 추가
        ClubHistory clubHistory = createClubHistory(club);
        club.addClubHistory(clubHistory);

        clubRepository.save(club);

        return ClubCreateResponse.builder()
                .clubId(club.getClubId())
                .build();
    }

    @Transactional
    public void registerClubAccount(Long clubId, ClubAccountRegisterRequest clubAccountRegisterRequest) {
        Member member = getMemberFromJwtInformation();
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new CustomException(CLUB_NOT_FOUND));

        if (!clubMemberRepository.existsByClubAndMemberAndClubMemberRole(club, member, PRESIDENT)) {
            throw new CustomException(CLUB_MEMBER_ROLE_NOT_ALLOWED);
        }

        ClubAccount clubAccount = createClubAccount(clubAccountRegisterRequest, club);
        clubAccountRepository.save(clubAccount);
    }

    @Transactional
    public ListWrapperResponse<ClubHistoryTermResponse> getClubHistory(Long clubId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new CustomException(CLUB_NOT_FOUND));

        List<ClubHistory> clubHistories = clubHistoryRepository.findByClub_ClubId(clubId);

        // ClubHistory를 ClubHistoryTermResponse로 변환
        List<ClubHistoryTermResponse> clubHistoryResponses = clubHistories.stream()
                .map(clubHistory -> ClubHistoryTermResponse.builder()
                        .clubHistoryUsageDate(clubHistory.getClubHistoryUsageDate())  // 적절한 필드 사용
                        .build())
                .collect(Collectors.toList());

        return ListWrapperResponse.of(clubHistoryResponses);
    }

    public ClubJoinGroupInfoResponse getClubJoinInfo(Long clubId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new CustomException(CLUB_NOT_FOUND));

        Group group = groupRepository.findByClubAndGroupType(club, JOIN)
                .orElseThrow(() -> new CustomException(GROUP_NOT_FOUND));

        return ClubJoinGroupInfoResponse.builder()
                .groupId(group.getGroupId())
                .groupName(group.getGroupName())
                .groupDescription(group.getGroupDescription())
                .groupJoinLink(group.getGroupJoinLink())
                .groupAmount(group.getGroupAmount())
                .build();
    }

    public List<ClubInfoResponse> getJoinedClubInfos() {
        Member member = getMemberFromJwtInformation();
        List<ClubMember> clubMembers = clubMemberRepository.findAllByMember(member);

        return clubMembers.stream()
                .map(ClubMember::getClub)
                .map(ClubInfoResponse::from)
                .toList();
    }

    public ClubInfoResponse findClubInfo(Long clubId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new CustomException(CLUB_NOT_FOUND));

        return ClubInfoResponse.from(club);
    }

    public ClubInfoResponse findClubInfoWithEnglishName(String clubName) {
        Club club = clubRepository.findByClubEnglishName(clubName)
                .orElseThrow(() -> new CustomException(CLUB_NOT_FOUND));

        return ClubInfoResponse.from(club);
    }

    private Club createClub(ClubCreateRequest clubCreateRequest, School school) {
        validateClubWithNames(clubCreateRequest.clubName(), clubCreateRequest.clubEnglishName());
        return Club.builder()
                .clubName(clubCreateRequest.clubName())
                .clubEnglishName(clubCreateRequest.clubEnglishName())
                .clubImage(clubCreateRequest.clubImage())
                .clubDescription(clubCreateRequest.clubDescription())
                .clubRoom(clubCreateRequest.clubRoom())
                .clubGeneration(clubCreateRequest.clubGeneration())
                .clubDues(clubCreateRequest.clubDues())
                .school(school)
                .build();
    }

    private Group createJoinGroup(Club club, ClubCreateRequest clubCreateRequest) {
        return Group.builder()
                .groupJoinLink("https://woohakdong.com/clubs/" + club.getClubEnglishName())
                .club(club)
                .groupAmount(club.getClubDues())
                .groupType(JOIN)
                .groupName(club.getClubName())
                .groupDescription(club.getClubName() + "의 " + club.getClubGeneration() + "기 동아리 가입하기")
                .groupChatLink(clubCreateRequest.clubGroupChatLink())
                .groupChatPassword(clubCreateRequest.clubGroupChatPassword())
                .build();
    }

    private ClubHistory createClubHistory(Club club) {
        return ClubHistory.builder()
                .club(club)
                .clubHistoryUsageDate(getAssignedTerm())
                .build();
    }

    private Member getMemberFromJwtInformation() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String provideId = userDetails.getUsername();

        return memberRepository.findByMemberProvideId(provideId)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
    }

    private ClubAccount createClubAccount(ClubAccountRegisterRequest clubAccountRegisterRequest, Club club) {
        String bankCode = getBankCode(clubAccountRegisterRequest.clubAccountBankName());

        return ClubAccount.builder()
                .clubAccountBankName(clubAccountRegisterRequest.clubAccountBankName())
                .clubAccountNumber(clubAccountRegisterRequest.clubAccountNumber())
                .clubAccountPinTechNumber(clubAccountRegisterRequest.clubAccountPinTechNumber())
                .clubAccountBankCode(bankCode)
                .club(club)
                .build();
    }

    private ClubMember createClubMember(Member member, Club club, ClubMemberRole role) {
        return ClubMember.builder()
                .clubMemberRole(role)
                .member(member)
                .club(club)
                .clubMemberAssignedTerm(getAssignedTerm())
                .clubJoinedDate(LocalDate.now())
                .build();
    }

    private LocalDate getAssignedTerm() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int semester = now.getMonthValue() <= 6 ? 1 : 7; // 1: 1학기, 7: 2학기
        return LocalDate.of(year, semester, 1);
    }

    private String getBankCode(String bankName) {
        switch (bankName) {
            case "농협은행":
                return "011";
            case "농협상호금융":
                return "012";
            case "산업은행":
                return "002";
            case "기업은행":
                return "003";
            case "국민은행":
                return "004";
            case "KEB하나은행":
                return "081";
            case "우리은행":
                return "020";
            case "SC제일은행":
                return "023";
            case "시티은행":
                return "027";
            case "대구은행":
                return "032";
            case "광주은행":
                return "034";
            case "제주은행":
                return "035";
            case "전북은행":
                return "037";
            case "경남은행":
                return "039";
            case "새마을금고":
                return "045";
            case "신한은행":
                return "088";
            case "카카오뱅크":
                return "090";
            default:
                throw new CustomException(INVALID_BANK_NAME);
        }
    }

}
