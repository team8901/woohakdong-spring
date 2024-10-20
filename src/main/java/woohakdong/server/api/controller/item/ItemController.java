package woohakdong.server.api.controller.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import woohakdong.server.api.controller.ListWrapperResponse;
import woohakdong.server.api.controller.item.dto.*;
import woohakdong.server.api.service.item.ItemService;

import java.util.List;

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
    public ListWrapperResponse<ItemListResponse> getItems(@PathVariable Long clubId) {
        List<ItemListResponse> items = itemService.getItemsByClubId(clubId);
        return ListWrapperResponse.of(items);
    }

    @PostMapping("/{clubId}/items/{itemId}/borrow")
    public ItemBorrowResponse borrowItem(@PathVariable Long clubId, @PathVariable Long itemId) {
        return itemService.borrowItem(clubId, itemId);
    }

    @PostMapping("/{clubId}/items/{itemId}/return")
    public void returnItem(@PathVariable Long clubId, @PathVariable Long itemId,
                           @RequestBody ItemReturnRequest request) {

        itemService.returnItem(clubId, itemId, request);
    }
}
