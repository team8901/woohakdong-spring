package woohakdong.server.api.controller.item;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import woohakdong.server.api.controller.ListWrapperResponse;
import woohakdong.server.api.controller.SliceResponse;
import woohakdong.server.api.controller.item.dto.*;

@Tag(name = "Item", description = "물품 관련 API")
public interface ItemControllerDocs {

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "물품 등록", description = "동아리 임원진 이상이 물품을 등록하면 물품 Id를 반환한다.")
    @ApiResponse(responseCode = "200", description = "물품 등록 성공", useReturnTypeSchema = true)
    public ItemRegisterResponse registerItem(@PathVariable Long clubId, @RequestBody ItemRegisterRequest request);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "물품 리스트 반환", description = "카테고리, 물품 검색에 따른 물품 리스트를 반환한다. 없을 경우 물품 리스트 전부 반환")
    @ApiResponse(responseCode = "200", description = "물품 리스트 반환 성공", useReturnTypeSchema = true)
    public SliceResponse<ItemResponse> getItems(@PathVariable Long clubId,
                                                @RequestParam(required = false) String keyword,
                                                @RequestParam(required = false) String category,
                                                @RequestParam(required = false) Boolean using,
                                                @RequestParam(required = false) Boolean available,
                                                @RequestParam(required = false) Boolean overdue,
                                                Pageable pageable);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "물품 상세정보 반환", description = "물품 상세정보 반환")
    @ApiResponse(responseCode = "200", description = "물품 상세정보 반환 성공", useReturnTypeSchema = true)
    public ItemInfoResponse getItemInfo(@PathVariable Long clubId, @PathVariable Long itemId);

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
    public SliceResponse<ItemHistoryResponse> getItemHistory(@PathVariable Long clubId, @PathVariable Long itemId, Pageable pageable);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "동아리별 물품 대여 기록 조회", description = "동아리별 물품 대여 기록을 조회할 수 있다.")
    @ApiResponse(responseCode = "200", description = "동아리별 물품 대여 기록 조회 성공", useReturnTypeSchema = true)
    public SliceResponse<ItemHistoryResponse> getAllItemHistory(@PathVariable Long clubId, Pageable pageable);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "물품 수정", description = "등록한 물품을 수정할 수 있다.")
    @ApiResponse(responseCode = "200", description = "물품 수정 성공", useReturnTypeSchema = true)
    public ItemUpdateResponse updateItem(@PathVariable Long clubId, @PathVariable Long itemId, @RequestBody ItemUpdateRequest request);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "물품 삭제", description = "등록한 물품을 삭제할 수 있다.")
    @ApiResponse(responseCode = "200", description = "물품 삭제 성공", useReturnTypeSchema = true)
    public void deleteItem(@PathVariable Long clubId, @PathVariable Long itemId);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "물품 대여 가능 여부 on off", description = "물품 대여 가능 여부를 설정 할 수 있다.")
    @ApiResponse(responseCode = "200", description = "물품 대여 가능 여부 변환 성공", useReturnTypeSchema = true)
    public void updateItemAvailability(@PathVariable Long clubId,
                                       @PathVariable Long itemId,
                                       @RequestBody ItemAvailableUpdateRequest request);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "자신이 빌린 물품 리스트 반환", description = "동아리 회원이 빌린 물품 리스트를 반환할 수 있다.")
    @ApiResponse(responseCode = "200", description = "자신이 빌린 물품 리스트 반환 성공", useReturnTypeSchema = true)
    public SliceResponse<ItemBorrowedResponse> getMyBorrowedItems(@PathVariable Long clubId, Pageable pageable);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "자신이 빌린 물품 대여 기록 리스트 반환", description = "자신이 빌린 물품 대여 기록 리스트를 반환할 수 있다.")
    @ApiResponse(responseCode = "200", description = "자신이 빌린 물품 대여 기록 리스트 반환 성공", useReturnTypeSchema = true)
    public SliceResponse<ItemHistoryResponse> getMyHistoryItems(@PathVariable Long clubId, Pageable pageable);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "동아리 회원이 빌린 물품 대여 기록 리스트 반환", description = "동아리 회원이 빌린 물품 대여 기록 리스트를 반환할 수 있다.")
    @ApiResponse(responseCode = "200", description = "동아리 회원이 빌린 물품 대여 기록 리스트 반환 성공", useReturnTypeSchema = true)
    public SliceResponse<ItemHistoryResponse> getClubMemberHistoryItems(@PathVariable Long clubId,
                                                                              @PathVariable Long clubMemberId, Pageable pageable);
}
