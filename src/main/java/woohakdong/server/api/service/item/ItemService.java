package woohakdong.server.api.service.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woohakdong.server.api.controller.item.dto.ItemListResponse;
import woohakdong.server.api.controller.item.dto.ItemRegisterRequest;
import woohakdong.server.api.controller.item.dto.ItemRegisterResponse;
import woohakdong.server.common.exception.CustomErrorInfo;
import woohakdong.server.common.exception.CustomException;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.club.ClubRepository;
import woohakdong.server.domain.item.Item;
import woohakdong.server.domain.item.ItemRepository;

import java.util.List;
import java.util.stream.Collectors;

import static woohakdong.server.common.exception.CustomErrorInfo.CLUB_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ClubRepository clubRepository;

    public ItemRegisterResponse registerItem(Long clubId, ItemRegisterRequest request) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new CustomException(CLUB_NOT_FOUND));

        // Item 엔티티 생성
        Item item = Item.builder()
                .club(club)
                .itemRentalDate(null)  // 대여 기록이 없으므로 null
                .itemRentalTime(0)      // 초기 대여 횟수 0
                .itemLocation(request.itemLocation())
                .itemCategory(request.itemCategory())
                .itemRentalMaxDay(request.itemRentalMaxDay())
                .itemAvailable(true)    // 기본적으로 대여 가능 상태
                .itemUsing(false)       // 현재 대여 중이 아님
                .itemDescription(request.itemDescription())
                .itemPhoto(request.itemPhoto())
                .itemName(request.itemName())
                .build();

        // Item 저장
        Item savedItem = itemRepository.save(item);

        // Response 반환
        return ItemRegisterResponse.builder()
                .itemId(savedItem.getItemId())
                .itemName(savedItem.getItemName())
                .build();
    }

    @Transactional
    public List<ItemListResponse> getItemsByClubId(Long clubId) {
        List<Item> items = itemRepository.findByClubClubId(clubId);
        return items.stream()
                .map(item -> ItemListResponse.builder()
                        .itemId(item.getItemId())
                        .itemName(item.getItemName())
                        .itemPhoto(item.getItemPhoto())
                        .itemDescription(item.getItemDescription())
                        .itemLocation(item.getItemLocation())
                        .itemCategory(item.getItemCategory())
                        .itemRentalMaxDay(item.getItemRentalMaxDay())
                        .itemAvailable(item.getItemAvailable())
                        .itemUsing(item.getItemUsing())
                        .itemRentalDate(item.getItemRentalDate())
                        .build())
                .collect(Collectors.toList());
    }
}
