package woohakdong.server.api.service.admin.club;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woohakdong.server.api.controller.admin.club.dto.AdminItemHistoryResponse;
import woohakdong.server.api.controller.admin.club.dto.ClubMemberResponse;
import woohakdong.server.api.controller.admin.club.dto.ClubStartDateResponse;
import woohakdong.server.api.controller.admin.overall.dto.CountResponse;
import woohakdong.server.api.controller.clubMember.dto.ClubMemberInfoResponse;
import woohakdong.server.domain.ItemHistory.ItemHistory;
import woohakdong.server.domain.ItemHistory.ItemHistoryRepository;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.club.ClubRepository;
import woohakdong.server.domain.clubmember.ClubMember;
import woohakdong.server.domain.clubmember.ClubMemberRepository;
import woohakdong.server.domain.item.ItemRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminClubService {

    private final ClubRepository clubRepository;
    private final ClubMemberRepository clubMemberRepository;
    private final ItemRepository itemRepository;
    private final ItemHistoryRepository itemHistoryRepository;

    public List<ClubMemberResponse> getClubMembers(Long clubId, LocalDate assignedTerm) {
        Club club = clubRepository.getById(clubId);

        List<ClubMember> members;
        if (assignedTerm == null) {
            members = clubMemberRepository.getAllByClub(club);
        } else {
            members = clubMemberRepository.getByClubAndClubMemberAssignedTerm(club, assignedTerm);
        }

        return members.stream().map(ClubMemberResponse::from).toList();
    }

    public CountResponse getClubItemCount(Long clubId, LocalDate assignedTerm) {
        Club club = clubRepository.getById(clubId);

        Long itemCount;

        if (assignedTerm == null) {
            itemCount = itemRepository.countByClub(club);
        } else {
            itemCount = itemRepository.countByClubAndCreatedAtBefore(club, assignedTerm.plusMonths(6).atStartOfDay());
        }
        return CountResponse.from(itemCount);
    }

    public ClubStartDateResponse getClubOperationPeriod(Long clubId) {
        Club club = clubRepository.getById(clubId);

        return ClubStartDateResponse.from(club.getCreatedAt());
    }

    public List<AdminItemHistoryResponse> getItemHistory(Long clubId, LocalDate assignedTerm) {
        Club club = clubRepository.getById(clubId);

        LocalDateTime startTerm = assignedTerm != null ? assignedTerm.atStartOfDay() : null;
        LocalDateTime endTerm = assignedTerm != null ? assignedTerm.plusMonths(6).atStartOfDay() : null;

        List<ItemHistory> itemHistories;
        if (assignedTerm == null) {
            itemHistories = itemHistoryRepository.getByItemClub(club);
        } else {
            itemHistories = itemHistoryRepository.getByItemClubAndItemRentalDateBetween(club, startTerm, endTerm);
        }

        return itemHistories.stream()
                .map(itemHistory -> {
                    // assignedTerm이 null인 경우 itemRentalDate를 기반으로 생성
                    LocalDate calculatedAssignedTerm = assignedTerm != null
                            ? assignedTerm
                            : itemHistory.getItemRentalDate().toLocalDate();
                    return AdminItemHistoryResponse.from(
                            itemHistory,
                            itemHistory.getItem(),
                            calculatedAssignedTerm
                    );
                })
                .collect(Collectors.toList());
    }
}
