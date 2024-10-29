package woohakdong.server.api.service.item;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woohakdong.server.api.controller.ListWrapperResponse;
import woohakdong.server.api.controller.item.dto.*;
import woohakdong.server.common.exception.CustomErrorInfo;
import woohakdong.server.common.exception.CustomException;
import woohakdong.server.common.security.jwt.CustomUserDetails;
import woohakdong.server.domain.ItemHistory.ItemHistory;
import woohakdong.server.domain.ItemHistory.ItemHistoryRepository;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.club.ClubRepository;
import woohakdong.server.domain.item.Item;
import woohakdong.server.domain.item.ItemCategory;
import woohakdong.server.domain.item.ItemRepository;
import woohakdong.server.domain.member.Member;
import woohakdong.server.domain.member.MemberRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static woohakdong.server.common.exception.CustomErrorInfo.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ClubRepository clubRepository;
    private final MemberRepository memberRepository;
    private final ItemHistoryRepository itemHistoryRepository;

    @Transactional
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

    @Transactional(readOnly = true)
    public List<ItemListResponse> getItemsByFilters(Long clubId, String keyword, String category) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new CustomException(CLUB_NOT_FOUND));

        List<Item> items;

        // 조건에 따른 분기 처리
        if ((keyword == null || keyword.isEmpty()) && (category == null || category.isEmpty())) {
            // 카테고리와 검색어 모두 없을 경우, 모든 물품 반환
            items = itemRepository.findByClubClubId(clubId);
        } else if (keyword == null || keyword.isEmpty()) {
            // 카테고리는 있고 검색어는 없을 경우, 카테고리로만 필터링
            ItemCategory itemCategory = ItemCategory.valueOf(category.toUpperCase());
            items = itemRepository.findByClubClubIdAndItemCategory(clubId, itemCategory);
        } else if (category == null || category.isEmpty()) {
            // 검색어는 있고 카테고리는 없을 경우, 검색어로만 필터링
            items = itemRepository.findItemsByClubIdAndNameContaining(clubId, keyword);
        } else {
            // 카테고리와 검색어 둘 다 있을 경우, 둘 다 필터링
            ItemCategory itemCategory = ItemCategory.valueOf(category.toUpperCase());
            items = itemRepository.findByClubIdAndItemNameContainingAndItemCategory(clubId, keyword, itemCategory);
        }

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
                        .itemRentalTime(item.getItemRentalTime())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public ItemBorrowResponse borrowItem(Long clubId, Long itemId) {
        Member member = getMemberFromJwtInformation();

        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new CustomException(CLUB_NOT_FOUND));

        Item item = itemRepository.findByIdForUpdate(itemId)
                .orElseThrow(() -> new CustomException(ITEM_NOT_FOUND));

        // 물품이 대여 가능 상태인지 확인
        if (!item.getItemAvailable()) {
            throw new CustomException(ITEM_NOT_AVAILABLE);
        } else if (item.getItemUsing()) {
            throw new CustomException(ITEM_USING);
        }

        // 물품 대여 처리
        item.setBorrow(true, LocalDateTime.now(), item.getItemRentalTime() + 1);

        // 대여 기록 추가
        ItemHistory itemHistory = ItemHistory.builder()
                .item(item)
                .member(member)
                .itemRentalDate(LocalDateTime.now())
                .itemReturnDate(null)
                .itemDueDate(LocalDateTime.now().plusDays(item.getItemRentalMaxDay()))  // 대여 기간 설정
                .build();

        itemHistoryRepository.save(itemHistory);

        return ItemBorrowResponse.builder()
                .itemId(item.getItemId())
                .itemHistoryId(itemHistory.getItemHistoryId())
                .itemRentalDate(itemHistory.getItemRentalDate())
                .itemDueDate(itemHistory.getItemDueDate())
                .build();
    }

    @Transactional
    public ItemReturnResponse returnItem(Long clubId, Long itemId, ItemReturnRequest request) {
        Member member = getMemberFromJwtInformation();

        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new CustomException(CLUB_NOT_FOUND));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new CustomException(ITEM_NOT_FOUND));

        if (!item.getItemUsing()) {
            throw new CustomException(ITEM_NOT_USING);
        }

        // 대여 기록 찾기 (반납 기록이 없는 대여 기록을 찾음)
        ItemHistory itemHistory = itemHistoryRepository.findActiveBorrowingRecord(item.getItemId(), member.getMemberId())
                .orElseThrow(() -> new CustomException(ITEM_HISTORY_NOT_FOUND));

        // 반납 처리
        itemHistory.setItemReturnDate(LocalDateTime.now());  // 반납 시간 설정

        if (request.itemReturnImage() != null) {
            itemHistory.setItemReturnImage(request.itemReturnImage());  // 반납 사진 저장 (선택 사항)
        }

        // 물품 상태 변경
        item.setItemUsing(false);

        // 추가적으로 연체 여부 확인 가능
        if (LocalDateTime.now().isAfter(itemHistory.getItemDueDate())) {
            // 연체 처리 로직 추가 가능
        }

        return ItemReturnResponse.builder()
                .itemHistoryId(itemHistory.getItemHistoryId())
                .itemId(item.getItemId())
                .itemReturnDate(itemHistory.getItemReturnDate())
                .build();
    }

    public ListWrapperResponse<ItemHistoryResponse> getItemHistory(Long clubId, Long itemId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new CustomException(CLUB_NOT_FOUND));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new CustomException(ITEM_NOT_FOUND));

        List<ItemHistoryResponse> historyResponses = itemHistoryRepository.findByItemAndClub(itemId, clubId).stream()
                .map(history -> ItemHistoryResponse.builder()
                        .itemHistoryId(history.getItemHistoryId())
                        .memberId(history.getMember().getMemberId())
                        .memberName(history.getMember().getMemberName())
                        .itemRentalDate(history.getItemRentalDate())
                        .itemDueDate(history.getItemDueDate())
                        .itemReturnDate(history.getItemReturnDate())
                        .itemReturnImage(history.getItemReturnImage())
                        .build())
                .collect(Collectors.toList());

        return ListWrapperResponse.of(historyResponses);
    }

    @Transactional
    public ItemUpdateResponse updateItem(Long clubId, Long itemId, ItemUpdateRequest request) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new CustomException(CLUB_NOT_FOUND));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new CustomException(ITEM_NOT_FOUND));

        item.updateItem(request.itemName(), request.itemPhoto(), request.itemDescription(),
                request.itemLocation(), request.itemCategory(), request.itemRentalMaxDay());

        return ItemUpdateResponse.builder()
                .itemId(item.getItemId())
                .build();
    }

    @Transactional
    public void deleteItem(Long clubId, Long itemId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new CustomException(CLUB_NOT_FOUND));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new CustomException(ITEM_NOT_FOUND));

        itemRepository.delete(item);
    }

    @Transactional
    public void updateItemAvailability(Long clubId, Long itemId, ItemAvailableUpdateRequest request) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new CustomException(CLUB_NOT_FOUND));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new CustomException(ITEM_NOT_FOUND));

        item.setItemAvailable(request.itemAvailable());
    }

    private Member getMemberFromJwtInformation() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String provideId = userDetails.getUsername();

        return memberRepository.findByMemberProvideId(provideId)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
    }
}
