package woohakdong.server.api.controller.item;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import woohakdong.server.api.controller.ListWrapperResponse;
import woohakdong.server.api.controller.item.dto.*;

@Tag(name = "Item", description = "물품 관련 API")
public interface ItemControllerDocs {

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "물품 등록", description = "동아리 임원진 이상이 물품을 등록하면 물품 Id를 반환한다.")
    @ApiResponse(responseCode = "200", description = "물품 등록 성공", useReturnTypeSchema = true)
    public ItemRegisterResponse registerItem(@PathVariable Long clubId, @RequestBody ItemRegisterRequest request);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "물품 리스트 반환", description = "물품 리스트를 반환한다.")
    @ApiResponse(responseCode = "200", description = "물품 리스트 반환 성공", useReturnTypeSchema = true)
    public ListWrapperResponse<ItemListResponse> getItems(@PathVariable Long clubId);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "물품 대여", description = "물품이 사용중이 아니라면 대여할 수 있다.")
    @ApiResponse(responseCode = "200", description = "물품 대여 성공", useReturnTypeSchema = true)
    public ItemBorrowResponse borrowItem(@PathVariable Long clubId, @PathVariable Long itemId);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "물품 반납", description = "물품을 반납할 수 있다.")
    @ApiResponse(responseCode = "200", description = "물품 반납 성공", useReturnTypeSchema = true)
    public ItemReturnResponse returnItem(@PathVariable Long clubId, @PathVariable Long itemId, @RequestBody ItemReturnRequest request);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "물품 대여 기록 조회", description = "물품 대여 기록을 조회할 수 있다.")
    @ApiResponse(responseCode = "200", description = "물품 대여 기록 조회 성공", useReturnTypeSchema = true)
    public ListWrapperResponse<ItemHistoryResponse> getItemHistory(@PathVariable Long clubId, @PathVariable Long itemId);
}
