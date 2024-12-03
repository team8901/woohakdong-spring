package woohakdong.server.domain.ItemHistory;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import woohakdong.server.common.exception.CustomErrorInfo;
import woohakdong.server.common.exception.CustomException;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.clubmember.ClubMember;
import woohakdong.server.domain.item.Item;
import woohakdong.server.domain.member.Member;

@RequiredArgsConstructor
@Repository
public class ItemHistoryRepositoryImpl implements ItemHistoryRepository{

    private final ItemHistoryJpaRepository itemHistoryJpaRepository;

    @Override
    public ItemHistory save(ItemHistory itemHistory) {
        return itemHistoryJpaRepository.save(itemHistory);
    }

    @Override
    public ItemHistory getById(Long itemHistoryId) {
        return itemHistoryJpaRepository.findById(itemHistoryId)
                .orElseThrow(() -> new CustomException(CustomErrorInfo.ITEM_HISTORY_NOT_FOUND));
    }

    @Override
    public ItemHistory getActiveBorrowingRecord(Item item, ClubMember clubMember) {
        return itemHistoryJpaRepository.findByItemAndClubMemberAndItemReturnDateIsNull(item, clubMember)
                .orElseThrow(() -> new CustomException(CustomErrorInfo.ITEM_HISTORY_NOT_FOUND));
    }

    @Override
    public List<ItemHistory> getAllByItem(Item item) {
        return itemHistoryJpaRepository.findByItemOrderByItemRentalDateDesc(item);
    }

    @Override
    public List<ItemHistory> getAllByMember(ClubMember clubMember) {
        return itemHistoryJpaRepository.findByClubMemberOrderByItemRentalDateDesc(clubMember);
    }

    @Override
    public List<ItemHistory> getAllByClubAndMember(Club club, ClubMember clubMember) {
        return itemHistoryJpaRepository.findByItemClubAndClubMemberOrderByItemRentalDateDesc(club, clubMember);
    }

    @Override
    public ItemHistory getByItemAndItemReturnDateIsNull(Item item) {
        return itemHistoryJpaRepository.findByItemAndItemReturnDateIsNull(item)
                .orElseThrow(() -> new CustomException(CustomErrorInfo.ITEM_HISTORY_NOT_FOUND));
    }

    @Override
    public List<ItemHistory> getByItemClub(Club club) {
        return itemHistoryJpaRepository.findByItemClub(club);
    }

    @Override
    public List<ItemHistory> getByItemClubAndItemRentalDateBetween(Club club, LocalDateTime startDate, LocalDateTime endDate) {
        return itemHistoryJpaRepository.findByItemClubAndItemRentalDateBetween(club, startDate, endDate);
    }

    @Override
    public List<ItemHistory> getByClub(Club club) {
        return itemHistoryJpaRepository.findByClubOrderByItemRentalDateDesc(club);
    }
}
