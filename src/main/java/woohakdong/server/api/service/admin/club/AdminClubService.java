package woohakdong.server.api.service.admin.club;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woohakdong.server.api.controller.admin.club.dto.ClubMemberResponse;
import woohakdong.server.api.controller.admin.club.dto.ClubStartDateResponse;
import woohakdong.server.api.controller.admin.overall.dto.CountResponse;
import woohakdong.server.api.controller.clubMember.dto.ClubMemberInfoResponse;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.club.ClubRepository;
import woohakdong.server.domain.clubmember.ClubMember;
import woohakdong.server.domain.clubmember.ClubMemberRepository;
import woohakdong.server.domain.item.ItemRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminClubService {

    private final ClubRepository clubRepository;
    private final ClubMemberRepository clubMemberRepository;
    private final ItemRepository itemRepository;

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
            itemCount = itemRepository.countByClubAndCreatedAtAfter(club, assignedTerm.atStartOfDay());
        }
        return CountResponse.from(itemCount);
    }

    public ClubStartDateResponse getClubOperationPeriod(Long clubId) {
        Club club = clubRepository.getById(clubId);

        return ClubStartDateResponse.from(club.getCreatedAt());
    }
}
