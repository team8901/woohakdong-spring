package woohakdong.server.api.service.item;

import static woohakdong.server.common.exception.CustomErrorInfo.ITEM_NOT_AVAILABLE;
import static woohakdong.server.common.exception.CustomErrorInfo.ITEM_NOT_USING;
import static woohakdong.server.common.exception.CustomErrorInfo.ITEM_USING;
import static woohakdong.server.common.exception.CustomErrorInfo.MEMBER_NOT_FOUND;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woohakdong.server.api.controller.ListWrapperResponse;
import woohakdong.server.api.controller.item.dto.*;
import woohakdong.server.common.exception.CustomException;
import woohakdong.server.common.security.jwt.CustomUserDetails;
import woohakdong.server.domain.ItemHistory.ItemHistory;
import woohakdong.server.domain.ItemHistory.ItemHistoryRepository;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.club.ClubRepository;
import woohakdong.server.domain.clubmember.ClubMember;
import woohakdong.server.domain.clubmember.ClubMemberRepository;
import woohakdong.server.domain.item.Item;
import woohakdong.server.domain.item.ItemCategory;
import woohakdong.server.domain.item.ItemRepository;
import woohakdong.server.domain.itemBorrowed.ItemBorrowed;
import woohakdong.server.domain.itemBorrowed.ItemBorrowedRepository;
import woohakdong.server.domain.member.Member;
import woohakdong.server.domain.member.MemberRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ClubRepository clubRepository;
    private final MemberRepository memberRepository;
    private final ItemHistoryRepository itemHistoryRepository;
    private final ClubMemberRepository clubMemberRepository;
    private final ItemBorrowedRepository itemBorrowedRepository;

    @Transactional
    public ItemRegisterResponse registerItem(Long clubId, ItemRegisterRequest request) {
        Club club = clubRepository.getById(clubId);

        Item item = Item.create(club, request.itemName(), request.itemPhoto(), request.itemDescription(),
                request.itemLocation(), request.itemCategory(), request.itemRentalMaxDay());

        Item savedItem = itemRepository.save(item);

        return ItemRegisterResponse.of(savedItem);
    }

    @Transactional(readOnly = true)
    public List<ItemResponse> getItemsByFilters(Long clubId, String keyword, String category) {
        Club club = clubRepository.getById(clubId);

        List<Item> items;

        // 조건에 따른 분기 처리
        if ((keyword == null || keyword.isEmpty()) && (category == null || category.isEmpty())) {
            // 카테고리와 검색어 모두 없을 경우, 모든 물품 반환
            items = itemRepository.getAllByClub(club);
        } else if (keyword == null || keyword.isEmpty()) {
            // 카테고리는 있고 검색어는 없을 경우, 카테고리로만 필터링
            ItemCategory itemCategory = ItemCategory.valueOf(category.toUpperCase());
            items = itemRepository.getAllByClubAndItemCategory(club, itemCategory);
        } else if (category == null || category.isEmpty()) {
            // 검색어는 있고 카테고리는 없을 경우, 검색어로만 필터링
            items = itemRepository.getAllByClubAndNameContaining(club, keyword);
        } else {
            // 카테고리와 검색어 둘 다 있을 경우, 둘 다 필터링
            ItemCategory itemCategory = ItemCategory.valueOf(category.toUpperCase());
            items = itemRepository.getAllByClubAndItemNameAndItemCategoryContaining(club, itemCategory, keyword);
        }

        return items.stream()
                .map(ItemResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public ItemBorrowResponse borrowItem(Long clubId, Long itemId) {
        Member member = getMemberFromJwtInformation();
        Club club = clubRepository.getById(clubId);
        Item item = itemRepository.getByIdForUpdate(itemId);
        LocalDate assignedTerm = getAssignedTerm();
        ClubMember clubMember = clubMemberRepository.getByClubAndMemberAndAssignedTerm(club, member, assignedTerm);

        // 물품이 대여 가능 상태인지 확인
        if (!item.getItemAvailable()) {
            throw new CustomException(ITEM_NOT_AVAILABLE);
        } else if (item.getItemUsing()) {
            throw new CustomException(ITEM_USING);
        }

        // 물품 대여 처리
        item.setBorrow(true, LocalDateTime.now(), item.getItemRentalTime() + 1);

        // 대여 기록 추가
        LocalDateTime borrowDate = LocalDateTime.now();
        LocalDateTime dueDate = borrowDate.plusDays(item.getItemRentalMaxDay());
        ItemHistory itemHistory = ItemHistory.create(member, item, borrowDate, dueDate);
        itemHistoryRepository.save(itemHistory);

        ItemBorrowed itemBorrowed = ItemBorrowed.createItemBorrowed(clubMember, item, item.getItemRentalDate().plusDays(item.getItemRentalMaxDay()));
        itemBorrowedRepository.save(itemBorrowed);

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

        Club club = clubRepository.getById(clubId);
        Item item = itemRepository.getById(itemId);

        if (!item.getItemUsing()) {
            throw new CustomException(ITEM_NOT_USING);
        }

        // 대여 기록 찾기 (반납 기록이 없는 대여 기록을 찾음)
        ItemHistory itemHistory = itemHistoryRepository.getActiveBorrowingRecord(item, member);

        // 반납 처리
        itemHistory.setItemReturnDate(LocalDateTime.now());  // 반납 시간 설정

        if (request.itemReturnImage() != null) {
            itemHistory.setItemReturnImage(request.itemReturnImage());  // 반납 사진 저장 (선택 사항)
        }

        // 물품 상태 변경
        item.setItemUsing(false);

        ItemBorrowed itemBorrowed = itemBorrowedRepository.findByItem(item);
        itemBorrowedRepository.delete(itemBorrowed);

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

    public List<ItemHistoryResponse> getItemHistory(Long clubId, Long itemId) {
        Club club = clubRepository.getById(clubId);
        Item item = itemRepository.getById(itemId);

        List<ItemHistoryResponse> historyResponses = itemHistoryRepository.getAllByItem(item).stream()
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

        return historyResponses;
    }

    public ItemResponse getItemInfo(Long clubId, Long itemId) {
        Club club = clubRepository.getById(clubId);
        Item item = itemRepository.getById(itemId);

        return ItemResponse.of(item);
    }

    @Transactional
    public ItemUpdateResponse updateItem(Long clubId, Long itemId, ItemUpdateRequest request) {
        Club club = clubRepository.getById(clubId);
        Item item = itemRepository.getById(itemId);

        item.updateItem(request.itemName(), request.itemPhoto(), request.itemDescription(),
                request.itemLocation(), request.itemCategory(), request.itemRentalMaxDay());

        return ItemUpdateResponse.builder()
                .itemId(item.getItemId())
                .build();
    }

    @Transactional
    public void deleteItem(Long clubId, Long itemId) {
        Club club = clubRepository.getById(clubId);
        Item item = itemRepository.getById(itemId);

        itemRepository.delete(item);
    }

    @Transactional
    public void updateItemAvailability(Long clubId, Long itemId, ItemAvailableUpdateRequest request) {
        Club club = clubRepository.getById(clubId);
        Item item = itemRepository.getById(itemId);

        item.setItemAvailable(request.itemAvailable());
    }

    public ListWrapperResponse<ItemBorrowedResponse> getMyBorrowedItems(Long clubId) {
        Member member = getMemberFromJwtInformation();
        Club club = clubRepository.getById(clubId);
        LocalDate assignedTerm = getAssignedTerm();

        ClubMember clubMember = clubMemberRepository.getByClubAndMemberAndAssignedTerm(club, member, assignedTerm);
        List<ItemBorrowed> borrowedItems = itemBorrowedRepository.findByClubMember(clubMember);

        List<ItemBorrowedResponse> itemBorrowedResponses = borrowedItems.stream()
                .map(itemBorrowed -> ItemBorrowedResponse.from(itemBorrowed.getItem(), itemBorrowed.getItemBorrowedReturnDate()))
                .collect(Collectors.toList());

        return ListWrapperResponse.of(itemBorrowedResponses);
    }

    public ListWrapperResponse<ItemHistoryResponse> getMyHistoryItems(Long clubId) {
        Member member = getMemberFromJwtInformation();
        Club club = clubRepository.getById(clubId);

        List<ItemHistory> histories = itemHistoryRepository.getAllByMember(member);
        List<ItemHistoryResponse> itemHistoryResponses = histories.stream()
                .map(history -> ItemHistoryResponse.from(history, member))
                .collect(Collectors.toList());

        return ListWrapperResponse.of(itemHistoryResponses);
    }

    public ListWrapperResponse<ItemHistoryResponse> getClubMemberHistoryItems(Long clubId, Long clubMemberId) {
        Club club = clubRepository.getById(clubId);
        ClubMember clubMember = clubMemberRepository.getById(clubMemberId);

        List<ItemHistory> histories = itemHistoryRepository.getAllByMember(clubMember.getMember());

        List<ItemHistoryResponse> itemHistoryResponses = histories.stream()
                .map(history -> ItemHistoryResponse.from(history, clubMember.getMember()))
                .collect(Collectors.toList());

        return ListWrapperResponse.of(itemHistoryResponses);
    }

    private LocalDate getAssignedTerm() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int semester = now.getMonthValue() <= 6 ? 1 : 7; // 1: 1학기, 7: 2학기
        return LocalDate.of(year, semester, 1);
    }

    private Member getMemberFromJwtInformation() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String provideId = userDetails.getUsername();

        return memberRepository.findByMemberProvideId(provideId)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
    }
}
