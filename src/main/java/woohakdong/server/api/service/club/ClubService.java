package woohakdong.server.api.service.club;

import static woohakdong.server.common.exception.CustomErrorInfo.CLUB_MEMBER_ROLE_NOT_ALLOWED;
import static woohakdong.server.common.exception.CustomErrorInfo.CLUB_NAME_DUPLICATION;
import static woohakdong.server.common.exception.CustomErrorInfo.INVALID_BANK_NAME;
import static woohakdong.server.common.exception.CustomErrorInfo.MEMBER_NOT_FOUND;
import static woohakdong.server.domain.clubmember.ClubMemberRole.PRESIDENT;
import static woohakdong.server.domain.group.GroupType.JOIN;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import woohakdong.server.domain.group.Group;
import woohakdong.server.domain.group.GroupRepository;
import woohakdong.server.domain.member.Member;
import woohakdong.server.domain.member.MemberRepository;
import woohakdong.server.domain.school.School;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ClubService {

    public static final String WOOHAKDONG_CLUB_PREFIX = "https://www.woohakdong.com/clubs/";
    private final ClubRepository clubRepository;
    private final MemberRepository memberRepository;
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
    public ClubIdResponse registerClub(ClubCreateRequest request) {
        Member member = getMemberFromJwtInformation();
        School school = member.getSchool();

        validateClubWithNames(request.clubName(), request.clubEnglishName());

        Club club = Club.create(request.clubName(), request.clubEnglishName(), request.clubDescription(),
                request.clubImage(), request.clubRoom(), request.clubGeneration(), request.clubDues(),
                request.clubGroupChatLink(), request.clubGroupChatPassword(), school);

        Group group = Group.create(club.getClubName(),
                club.getClubName() + "의 " + club.getClubGeneration() + "기 동아리 가입하기", club.getClubDues(),
                WOOHAKDONG_CLUB_PREFIX + club.getClubEnglishName(), club.getClubGroupChatLink(),
                club.getClubGroupChatPassword(), JOIN, club);

        ClubMember clubMember = ClubMember.create(club, getAssignedTerm(), PRESIDENT, member);

        ClubHistory clubHistory = ClubHistory.create(club, getAssignedTerm());

        club.addGroup(group);
        club.addClubMember(clubMember);
        club.addClubHistory(clubHistory);
        clubRepository.save(club);

        return ClubIdResponse.from(club);
    }

    public ClubAccountResponse getClubAccount(Long clubId) {
        Club club = clubRepository.getById(clubId);
        ClubAccount clubAccount = clubAccountRepository.getByClub(club);

        return ClubAccountResponse.from(clubAccount);
    }

    @Transactional
    public void registerClubAccount(Long clubId, ClubAccountRegisterRequest request) {
        Member member = getMemberFromJwtInformation();
        Club club = clubRepository.getById(clubId);

        if (!clubMemberRepository.existsByClubAndMemberAndClubMemberRole(club, member, PRESIDENT)) {
            throw new CustomException(CLUB_MEMBER_ROLE_NOT_ALLOWED);
        }

        ClubAccount clubAccount = ClubAccount.create(club, request.clubAccountBankName(), request.clubAccountNumber(),
                request.clubAccountPinTechNumber(), getBankCode(request.clubAccountBankName()));

        clubAccountRepository.save(clubAccount);
    }

    public List<ClubHistoryTermResponse> getClubHistory(Long clubId) {
        Club club = clubRepository.getById(clubId);
        List<ClubHistory> clubHistories = clubHistoryRepository.getAllByClub(club);

        return clubHistories.stream()
                .map(ClubHistoryTermResponse::from)
                .toList();
    }

    public ClubJoinGroupInfoResponse getClubJoinInfo(Long clubId) {
        Club club = clubRepository.getById(clubId);
        Group group = groupRepository.getByClubAndGroupTypeAndGroupIsAvailable(club, JOIN, true);

        return ClubJoinGroupInfoResponse.from(group);
    }

    public List<ClubInfoResponse> getJoinedClubInfos() {
        Member member = getMemberFromJwtInformation();
        List<ClubMember> clubMembers = clubMemberRepository.getAllByMember(member);

        return clubMembers.stream()
                .map(ClubMember::getClub)
                .map(ClubInfoResponse::from)
                .toList();
    }

    public ClubInfoResponse findClubInfo(Long clubId) {
        Club club = clubRepository.getById(clubId);
        return ClubInfoResponse.from(club);
    }

    public ClubInfoResponse findClubInfoWithEnglishName(String clubName) {
        Club club = clubRepository.getByClubEnglishName(clubName);
        return ClubInfoResponse.from(club);
    }

    @Transactional
    public ClubInfoResponse updateClubInfo(Long clubId, ClubUpdateRequest request) {
        Club club = clubRepository.getById(clubId);
        club.update(request.clubImage(), request.clubDescription(), request.clubRoom(), request.clubGeneration(),
                request.clubGroupChatLink(), request.clubGroupChatPassword(), request.clubDues());

        Group group = groupRepository.getByClubAndGroupTypeAndGroupIsAvailable(club, JOIN, true);
        group.updateJoinGroup(club.getClubName() + "의 " + club.getClubGeneration() + "기 동아리 가입하기",
                club.getClubDues(), club.getClubGroupChatLink(), club.getClubGroupChatPassword());

        return ClubInfoResponse.from(club);
    }

    private Member getMemberFromJwtInformation() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String provideId = userDetails.getUsername();

        return memberRepository.findByMemberProvideId(provideId)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
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
