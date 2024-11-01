package woohakdong.server.domain.item;

import static woohakdong.server.common.exception.CustomErrorInfo.ITEM_NOT_FOUND;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import woohakdong.server.common.exception.CustomException;
import woohakdong.server.domain.club.Club;

@RequiredArgsConstructor
@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final ItemJpaRepository itemJpaRepository;

    @Override
    public Item save(Item item) {
        return itemJpaRepository.save(item);
    }

    @Override
    public Item getById(Long itemId) {
        return itemJpaRepository.findById(itemId)
                .orElseThrow(() -> new CustomException(ITEM_NOT_FOUND));
    }

    @Override
    public void delete(Item item) {
        itemJpaRepository.delete(item);
    }

    @Override
    public List<Item> getAllByClub(Club club) {
        return itemJpaRepository.findByClub(club);
    }

    @Override
    public Item getByIdForUpdate(Long itemId) {
        return itemJpaRepository.findByIdForUpdate(itemId)
                .orElseThrow(() -> new CustomException(ITEM_NOT_FOUND));
    }

    @Override
    public List<Item> getAllByClubAndItemCategory(Club club, ItemCategory category) {
        return itemJpaRepository.findAllByClubAndItemCategory(club, category);
    }

    @Override
    public List<Item> getAllByClubAndNameContaining(Club club, String itemName) {
        return itemJpaRepository.findAllByClubAndItemNameContaining(club, itemName);
    }


    @Override
    public List<Item> getAllByClubAndItemNameAndItemCategoryContaining(Club club, ItemCategory category,
                                                                       String keyword) {
        return itemJpaRepository.findAllByClubAndItemNameAndItemCategoryContaining(club, keyword, category);
    }

}