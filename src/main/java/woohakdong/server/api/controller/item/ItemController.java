package woohakdong.server.api.controller.item;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import woohakdong.server.api.controller.ListWrapperResponse;
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
import woohakdong.server.api.service.item.ItemService;


@RestController
@RequestMapping("/v1/clubs")
@RequiredArgsConstructor
public class ItemController implements ItemControllerDocs {

    private final ItemService itemService;

    @PostMapping("/{clubId}/items")
    public ItemRegisterResponse registerItem(@PathVariable Long clubId, @RequestBody ItemRegisterRequest request) {
        return itemService.registerItem(clubId, request);
    }

    @GetMapping("/{clubId}/items")
    public SliceResponse<ItemResponse> getItems(@PathVariable Long clubId,
                                                @RequestParam(required = false) String keyword,
                                                @RequestParam(required = false) String category,
                                                @RequestParam(required = false) Boolean using,
                                                @RequestParam(required = false) Boolean available,
                                                @RequestParam(required = false) Boolean overdue,
                                                Pageable pageable) {

        return itemService.getItemsByFilters(clubId, keyword, category, using, available, overdue, pageable);
    }

    @GetMapping("/{clubId}/items/{itemId}")
    public ItemInfoResponse getItemInfo(@PathVariable Long clubId, @PathVariable Long itemId) {
        return itemService.getItemInfo(clubId, itemId);
    }

    @PostMapping("/{clubId}/items/{itemId}/borrow")
    public ItemBorrowResponse borrowItem(@PathVariable Long clubId, @PathVariable Long itemId) {
        return itemService.borrowItem(clubId, itemId, LocalDate.now());
    }

    @PostMapping("/{clubId}/items/{itemId}/return")
    public ItemReturnResponse returnItem(@PathVariable Long clubId, @PathVariable Long itemId,
                                         @RequestBody ItemReturnRequest request) {
        return itemService.returnItem(clubId, itemId, request, LocalDate.now());
    }

    @GetMapping("/{clubId}/items/{itemId}/history")
    public SliceResponse<ItemHistoryResponse> getItemHistory(@PathVariable Long clubId,
                                                             @PathVariable Long itemId,
                                                             Pageable pageable) {
        Slice<ItemHistoryResponse> responses = itemService.getItemHistory(clubId, itemId, pageable);
        return SliceResponse.of(responses.getContent(), responses.getNumber(), responses.hasNext());
    }

    @GetMapping("/{clubId}/items/history")
    public SliceResponse<ItemHistoryResponse> getAllItemHistory(@PathVariable Long clubId, Pageable pageable) {
        Slice<ItemHistoryResponse> responses = itemService.getAllItemHistory(clubId, pageable);
        return SliceResponse.of(responses.getContent(), responses.getNumber(), responses.hasNext());
    }

    @PutMapping("/{clubId}/items/{itemId}")
    public ItemUpdateResponse updateItem(@PathVariable Long clubId, @PathVariable Long itemId,
                                         @RequestBody ItemUpdateRequest request) {
        return itemService.updateItem(clubId, itemId, request);
    }

    @DeleteMapping("/{clubId}/items/{itemId}")
    public void deleteItem(@PathVariable Long clubId, @PathVariable Long itemId) {
        itemService.deleteItem(clubId, itemId);
    }

    @PostMapping("/{clubId}/items/{itemId}/availability")
    public void updateItemAvailability(@PathVariable Long clubId,
                                       @PathVariable Long itemId,
                                       @RequestBody ItemAvailableUpdateRequest request) {
        itemService.updateItemAvailability(clubId, itemId, request);
    }

    @GetMapping("/{clubId}/items/borrowed")
    public SliceResponse<ItemBorrowedResponse> getMyBorrowedItems(@PathVariable Long clubId, Pageable pageable) {
        Slice<ItemBorrowedResponse> responses = itemService.getMyBorrowedItems(clubId, LocalDate.now(), pageable);
        return SliceResponse.of(responses.getContent(), responses.getNumber(), responses.hasNext());
    }

    @GetMapping("/{clubId}/items/history/my")
    public SliceResponse<ItemHistoryResponse> getMyHistoryItems(@PathVariable Long clubId, Pageable pageable) {
        Slice<ItemHistoryResponse> responses = itemService.getMyHistoryItems(clubId, LocalDate.now(), pageable);
        return SliceResponse.of(responses.getContent(), responses.getNumber(), responses.hasNext());
    }

    @GetMapping("/{clubId}/items/history/{clubMemberId}")
    public SliceResponse<ItemHistoryResponse> getClubMemberHistoryItems(@PathVariable Long clubId,
                                                                        @PathVariable Long clubMemberId,
                                                                        Pageable pageable) {
        Slice<ItemHistoryResponse> responses = itemService.getClubMemberHistoryItems(clubId, clubMemberId, pageable);
        return SliceResponse.of(responses.getContent(), responses.getNumber(), responses.hasNext());
    }
}
