package woohakdong.server.api.service.club;

import static woohakdong.server.common.exception.CustomErrorInfo.CLUB_EXPIRED;
import static woohakdong.server.common.exception.CustomErrorInfo.CLUB_MEMBER_ROLE_NOT_ALLOWED;
import static woohakdong.server.common.exception.CustomErrorInfo.CLUB_NAME_DUPLICATION;
import static woohakdong.server.common.exception.CustomErrorInfo.INVALID_BANK_NAME;
import static woohakdong.server.domain.clubmember.ClubMemberRole.PRESIDENT;
import static woohakdong.server.domain.group.GroupType.CLUB_PAYMENT;
import static woohakdong.server.domain.group.GroupType.JOIN;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woohakdong.server.api.controller.club.dto.ClubAccountRegisterRequest;
import woohakdong.server.api.controller.club.dto.ClubAccountResponse;
import woohakdong.server.api.controller.club.dto.ClubCreateRequest;
import woohakdong.server.api.controller.club.dto.ClubHistoryTermResponse;
import woohakdong.server.api.controller.club.dto.ClubIdResponse;
import woohakdong.server.api.controller.club.dto.ClubInfoResponse;
import woohakdong.server.api.controller.club.dto.ClubSummaryResponse;
import woohakdong.server.api.controller.club.dto.ClubUpdateRequest;
import woohakdong.server.api.controller.group.dto.GroupInfoResponse;
import woohakdong.server.common.exception.CustomException;
import woohakdong.server.common.util.date.DateUtil;
import woohakdong.server.common.util.security.SecurityUtil;
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
import woohakdong.server.domain.school.School;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ClubService {

    public static final String WOOHAKDONG_CLUB_PREFIX = "https://www.woohakdong.com/clubs/";

    private final SecurityUtil securityUtil;
    private final DateUtil dateUtil;

    private final ClubRepository clubRepository;
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
    public ClubIdResponse registerClub(ClubCreateRequest request, LocalDate date) {
        Member member = securityUtil.getMember();
        School school = member.getSchool();
        LocalDate assignedTerm = dateUtil.getAssignedTerm(date);
        LocalDate expirationDate = assignedTerm.plusMonths(6).minusDays(1);

        validateClubWithNames(request.clubName(), request.clubEnglishName());

        Club club = Club.create(request.clubName(), request.clubEnglishName(), request.clubDescription(),
                request.clubImage(), request.clubRoom(), request.clubGeneration(), request.clubDues(),
                request.clubGroupChatLink(), request.clubGroupChatPassword(), expirationDate, school);

        String groupDescription = club.getClubName() + "신규 가입";
        if (groupDescription.length() > 15) {
            groupDescription = groupDescription.substring(0, 15);
        }

        Group group = Group.create(club.getClubName(), groupDescription, club.getClubDues(),
                WOOHAKDONG_CLUB_PREFIX + club.getClubEnglishName(), club.getClubGroupChatLink(),
                club.getClubGroupChatPassword(), JOIN, club);

        ClubMember clubMember = ClubMember.create(club, assignedTerm, PRESIDENT, member);
        ClubHistory clubHistory = ClubHistory.create(club, assignedTerm);

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
    public void registerClubAccount(Long clubId, ClubAccountRegisterRequest request, LocalDate date) {
        Member member = securityUtil.getMember();
        Club club = clubRepository.getById(clubId);

        if (!clubMemberRepository.existsByClubAndMemberAndClubMemberRole(club, member, PRESIDENT)) {
            throw new CustomException(CLUB_MEMBER_ROLE_NOT_ALLOWED);
        }

        ClubAccount clubAccount = ClubAccount.create(club, request.clubAccountBankName(), request.clubAccountNumber(),
                request.clubAccountPinTechNumber(), getBankCode(request.clubAccountBankName()),
                dateUtil.getAssignedTerm(date).atStartOfDay());

        clubAccountRepository.save(clubAccount);
    }

    public List<ClubHistoryTermResponse> getClubHistory(Long clubId) {
        Club club = clubRepository.getById(clubId);
        List<ClubHistory> clubHistories = clubHistoryRepository.getAllByClub(club);

        return clubHistories.stream()
                .map(ClubHistoryTermResponse::from)
                .toList();
    }

    public GroupInfoResponse getClubJoinInfo(Long clubId) {
        Club club = clubRepository.getById(clubId);
        Group group = groupRepository.getByClubAndGroupType(club, JOIN);

        return GroupInfoResponse.from(group);
    }

    public List<ClubInfoResponse> getJoinedClubInfos() {
        Member member = securityUtil.getMember();
        List<ClubMember> clubMembers = clubMemberRepository.getAllByMember(member);

        Set<Club> clubs = clubMembers.stream()
                .map(ClubMember::getClub)
                .collect(Collectors.toSet());

        return clubs.stream()
                .map(ClubInfoResponse::from)
                .toList();
    }

    public ClubInfoResponse findClubInfo(Long clubId) {
        Club club = clubRepository.getById(clubId);
        return ClubInfoResponse.from(club);
    }

    public ClubSummaryResponse findClubInfoWithEnglishName(String clubName) {
        Club club = clubRepository.getByClubEnglishName(clubName);
        return ClubSummaryResponse.from(club);
    }

    @Transactional
    public ClubInfoResponse updateClubInfo(Long clubId, ClubUpdateRequest request) {
        Club club = clubRepository.getById(clubId);
        club.update(request.clubImage(), request.clubDescription(), request.clubRoom(), request.clubGeneration(),
                request.clubGroupChatLink(), request.clubGroupChatPassword(), request.clubDues());

        Group group = groupRepository.getByClubAndGroupType(club, JOIN);
        group.updateJoinGroup(club.getClubDues(), club.getClubGroupChatLink(), club.getClubGroupChatPassword());

        return ClubInfoResponse.from(club);
    }

    @Transactional(noRollbackFor = CustomException.class)
    public void checkClubExpired(Long clubId, LocalDate date) {
        Club club = clubRepository.getById(clubId);
        LocalDate assignedTerm = dateUtil.getAssignedTerm(date).minusMonths(6);
        Integer clubMemberNumber = clubMemberRepository.countByClubAndAssignedTerm(club, assignedTerm);

        if (club.isExpired(date)) {
            if (!groupRepository.checkExistenceClubGroup(club, CLUB_PAYMENT)) {
                Group group = Group.createClubPaymentGroup(club, clubMemberNumber);
                club.addGroup(group);
            }
            throw new CustomException(CLUB_EXPIRED);
        }
    }

    public GroupInfoResponse getGroupPaymentInfo(Long clubId) {
        Club club = clubRepository.getById(clubId);
        Group group = groupRepository.getByClubAndGroupType(club, CLUB_PAYMENT);
        return GroupInfoResponse.from(group);
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
