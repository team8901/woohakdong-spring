package woohakdong.server.api.service.item;

import static woohakdong.server.common.exception.CustomErrorInfo.ITEM_NOT_AVAILABLE;
import static woohakdong.server.common.exception.CustomErrorInfo.ITEM_NOT_USING;
import static woohakdong.server.common.exception.CustomErrorInfo.ITEM_USING;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woohakdong.server.api.controller.SliceResponse;
import woohakdong.server.api.controller.item.dto.ItemAvailableUpdateRequest;
import woohakdong.server.api.controller.item.dto.ItemBorrowResponse;
import woohakdong.server.api.controller.item.dto.ItemBorrowedResponse;
import woohakdong.server.api.controller.item.dto.ItemHistoryResponse;
import woohakdong.server.api.controller.item.dto.ItemInfoResponse;
import woohakdong.server.api.controller.item.dto.ItemRegisterRequest;
import woohakdong.server.api.controller.item.dto.ItemRegisterResponse;
import woohakdong.server.api.controller.item.dto.ItemResponse;
import woohakdong.server.api.controller.item.dto.ItemReturnRequest;
import woohakdong.server.api.controller.item.dto.ItemReturnResponse;
import woohakdong.server.api.controller.item.dto.ItemUpdateRequest;
import woohakdong.server.api.controller.item.dto.ItemUpdateResponse;
import woohakdong.server.common.exception.CustomException;
import woohakdong.server.common.util.date.DateUtil;
import woohakdong.server.common.util.security.SecurityUtil;
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

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final SecurityUtil securityUtil;
    private final DateUtil dateUtil;

    private final ItemRepository itemRepository;
    private final ClubRepository clubRepository;
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
    public SliceResponse<ItemResponse> getItemsByFilters(Long clubId, String keyword, String category, Boolean using,
                                                         Boolean available, Boolean overdue, Pageable pageable) {
        Club club = clubRepository.getById(clubId);
        Slice<Item> items;

        if (keyword == null && category == null && using == null && available == null) {
            items = itemRepository.getAllByClub(club, pageable);
        } else {
            if (category == null || category.isEmpty()) {
                items = itemRepository.getItemsByFilters(club, keyword, null, using, available, pageable);
            } else {
                ItemCategory itemCategory = ItemCategory.valueOf(category.toUpperCase());
                items = itemRepository.getItemsByFilters(club, keyword, itemCategory, using, available, pageable);
            }
        }

        List<ItemResponse> responses = new ArrayList<>();

        if (overdue != null) {
            getItemByOverdue(overdue, items.getContent(), responses);
        } else {
            getItemByOhters(items.getContent(), responses);
        }

        return SliceResponse.of(responses, items.getNumber(), items.hasNext());
    }

    @Transactional
    public ItemBorrowResponse borrowItem(Long clubId, Long itemId, LocalDate date) {
        Member member = securityUtil.getMember();
        Club club = clubRepository.getById(clubId);
        Item item = itemRepository.getByIdForUpdate(itemId);
        LocalDate assignedTerm = dateUtil.getAssignedTerm(date);
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
        ItemHistory itemHistory = ItemHistory.create(clubMember, member.getMemberName(), item, borrowDate, dueDate, club);
        itemHistoryRepository.save(itemHistory);

        ItemBorrowed itemBorrowed = ItemBorrowed.createItemBorrowed(clubMember, item,
                item.getItemRentalDate().plusDays(item.getItemRentalMaxDay()));
        itemBorrowedRepository.save(itemBorrowed);

        return ItemBorrowResponse.builder()
                .itemId(item.getItemId())
                .itemHistoryId(itemHistory.getItemHistoryId())
                .itemRentalDate(itemHistory.getItemRentalDate())
                .itemDueDate(itemHistory.getItemDueDate())
                .build();
    }

    @Transactional
    public ItemReturnResponse returnItem(Long clubId, Long itemId, ItemReturnRequest request, LocalDate date) {
        Member member = securityUtil.getMember();
        Club club = clubRepository.getById(clubId);
        Item item = itemRepository.getById(itemId);
        LocalDate assignedTerm = dateUtil.getAssignedTerm(date);
        ClubMember clubMember = clubMemberRepository.getByClubAndMemberAndAssignedTerm(club, member, assignedTerm);

        if (!item.getItemUsing()) {
            throw new CustomException(ITEM_NOT_USING);
        }

        // 대여 기록 찾기 (반납 기록이 없는 대여 기록을 찾음)
        ItemHistory itemHistory = itemHistoryRepository.getActiveBorrowingRecord(item, clubMember);

        // 반납 처리
        itemHistory.setItemReturnDate(LocalDateTime.now());  // 반납 시간 설정

        if (request.itemReturnImage() != null) {
            itemHistory.setItemReturnImage(request.itemReturnImage());  // 반납 사진 저장 (선택 사항)
        }

        // 물품 상태 변경
        item.setItemUsing(false);

        ItemBorrowed itemBorrowed = itemBorrowedRepository.getByItem(item);
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

    public Slice<ItemHistoryResponse> getItemHistory(Long clubId, Long itemId, Pageable pageable) {
        Club club = clubRepository.getById(clubId);
        Item item = itemRepository.getById(itemId);

        Slice<ItemHistory> itemHistories = itemHistoryRepository.getAllByItem(item, pageable);

        return itemHistories.map(history -> {
            Boolean isOverdue = history.getItemDueDate() != null && (
                    (history.getItemReturnDate() == null && history.getItemDueDate().isBefore(LocalDateTime.now())) || // 반납되지 않았고 연체
                            (history.getItemReturnDate() != null && history.getItemReturnDate().isAfter(history.getItemDueDate())) // 반납이 연체된 날짜에 이루어짐
            );

            return ItemHistoryResponse.from(
                    history,
                    history.getClubMember().getClubMemberId(),
                    item,
                    isOverdue
            );
        });
    }

    public Slice<ItemHistoryResponse> getAllItemHistory(Long clubId, Pageable pageable) {
        Club club = clubRepository.getById(clubId);

        Slice<ItemHistory> itemHistories = itemHistoryRepository.getByClub(club, pageable);

        return itemHistories.map(history -> {
            Boolean isOverdue = history.getItemDueDate() != null && (
                    (history.getItemReturnDate() == null && history.getItemDueDate().isBefore(LocalDateTime.now())) || // 반납되지 않았고 연체
                            (history.getItemReturnDate() != null && history.getItemReturnDate().isAfter(history.getItemDueDate())) // 반납이 연체된 날짜에 이루어짐
            );

            return ItemHistoryResponse.from(
                    history,
                    history.getClubMember().getClubMemberId(),
                    history.getItem(),
                    isOverdue
            );
        });
    }

    public ItemInfoResponse getItemInfo(Long clubId, Long itemId) {
        Club club = clubRepository.getById(clubId);
        Item item = itemRepository.getById(itemId);

        return ItemInfoResponse.of(item);
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

    public Slice<ItemBorrowedResponse> getMyBorrowedItems(Long clubId, LocalDate date, Pageable pageable) {
        Member member = securityUtil.getMember();
        Club club = clubRepository.getById(clubId);
        LocalDate assignedTerm = dateUtil.getAssignedTerm(date);

        ClubMember clubMember = clubMemberRepository.getByClubAndMemberAndAssignedTerm(club, member, assignedTerm);
        Slice<ItemBorrowed> borrowedItems = itemBorrowedRepository.getByClubMember(clubMember, pageable);

        return borrowedItems.map(itemBorrowed ->
                ItemBorrowedResponse.from(itemBorrowed.getItem(), itemBorrowed.getItemBorrowedReturnDate())
        );
    }

    public Slice<ItemHistoryResponse> getMyHistoryItems(Long clubId, LocalDate date, Pageable pageable) {
        Member member = securityUtil.getMember();
        Club club = clubRepository.getById(clubId);
        LocalDate assignedTerm = dateUtil.getAssignedTerm(date);
        ClubMember clubMember = clubMemberRepository.getByClubAndMemberAndAssignedTerm(club, member, assignedTerm);

        Slice<ItemHistory> histories = itemHistoryRepository.getAllByMember(clubMember, pageable);

        return histories.map(history -> {
            Boolean isOverdue = history.getItemDueDate() != null && (
                    (history.getItemReturnDate() == null && history.getItemDueDate().isBefore(LocalDateTime.now())) ||
                            (history.getItemReturnDate() != null && history.getItemReturnDate().isAfter(history.getItemDueDate()))
            );

            return ItemHistoryResponse.from(
                    history,
                    history.getClubMember().getClubMemberId(),
                    history.getItem(),
                    isOverdue
            );
        });
    }

    public Slice<ItemHistoryResponse> getClubMemberHistoryItems(Long clubId, Long clubMemberId, Pageable pageable) {
        Club club = clubRepository.getById(clubId);
        ClubMember clubMember = clubMemberRepository.getById(clubMemberId);

        Slice<ItemHistory> histories = itemHistoryRepository.getAllByClubAndMember(club, clubMember, pageable);

        return histories.map(history -> {
            Boolean isOverdue = history.getItemDueDate() != null && (
                    (history.getItemReturnDate() == null && history.getItemDueDate().isBefore(LocalDateTime.now())) || // 반납되지 않았고 연체
                            (history.getItemReturnDate() != null && history.getItemReturnDate().isAfter(history.getItemDueDate())) // 반납이 연체된 날짜에 이루어짐
            );

            return ItemHistoryResponse.from(
                    history,
                    history.getClubMember().getClubMemberId(),
                    history.getItem(),
                    isOverdue
            );
        });
    }


    private void getItemByOhters(List<Item> items, List<ItemResponse> responses) {
        for (Item item : items) {
            ItemResponse itemResponse;
            if (Boolean.TRUE.equals(item.getItemUsing())) {
                ItemHistory history = itemHistoryRepository.getByItemAndItemReturnDateIsNull(item);
                if (history.getItemDueDate().isBefore(LocalDateTime.now())) {
                    itemResponse = ItemResponse.of(item, history.getMemberName(), true);
                } else {
                    itemResponse = ItemResponse.of(item, history.getMemberName(), false);
                }
            } else {
                itemResponse = ItemResponse.of(item, null, false);
            }
            responses.add(itemResponse);
        }
    }

    private void getItemByOverdue(Boolean overdue, List<Item> items, List<ItemResponse> responses) {
        for (Item item : items) {
            ItemResponse itemResponse = null;
            if (Boolean.TRUE.equals(overdue)) {
                if (Boolean.TRUE.equals(item.getItemUsing())) {
                    ItemHistory history = itemHistoryRepository.getByItemAndItemReturnDateIsNull(item);
                    if (history.getItemDueDate().isBefore(LocalDateTime.now())) {
                        itemResponse = ItemResponse.of(item, history.getMemberName(), true);
                    }
                }
            } else {
                if (Boolean.TRUE.equals(item.getItemUsing())) {
                    ItemHistory history = itemHistoryRepository.getByItemAndItemReturnDateIsNull(item);
                    if (history.getItemDueDate().isAfter(LocalDateTime.now())) {
                        itemResponse = ItemResponse.of(item, history.getMemberName(), false);
                    }
                } else {
                    itemResponse = ItemResponse.of(item, null, false);
                }
            }
            if (itemResponse != null) {
                responses.add(itemResponse);
            }
        }
    }
}
