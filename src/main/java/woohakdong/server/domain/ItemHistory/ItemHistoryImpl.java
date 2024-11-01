package woohakdong.server.domain.ItemHistory;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import woohakdong.server.common.exception.CustomErrorInfo;
import woohakdong.server.common.exception.CustomException;
import woohakdong.server.domain.item.Item;
import woohakdong.server.domain.member.Member;

@RequiredArgsConstructor
@Repository
public class ItemHistoryImpl implements ItemHistoryRepository{

    private final ItemJpaHistoryRepository itemJpaHistoryRepository;

    @Override
    public ItemHistory save(ItemHistory itemHistory) {
        return itemJpaHistoryRepository.save(itemHistory);
    }

    @Override
    public ItemHistory getById(Long itemHistoryId) {
        return itemJpaHistoryRepository.findById(itemHistoryId)
                .orElseThrow(() -> new CustomException(CustomErrorInfo.ITEM_HISTORY_NOT_FOUND));
    }

    @Override
    public ItemHistory getActiveBorrowingRecord(Item item, Member member) {
        return itemJpaHistoryRepository.findByItemAndMemberAndItemReturnDateIsNull(item, member)
                .orElseThrow(() -> new CustomException(CustomErrorInfo.ITEM_HISTORY_NOT_FOUND));
    }

    @Override
    public List<ItemHistory> getAllByItem(Item item) {
        return itemJpaHistoryRepository.findByItem(item);
    }
}
