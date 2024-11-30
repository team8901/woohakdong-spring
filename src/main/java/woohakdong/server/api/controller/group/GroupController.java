package woohakdong.server.api.controller.group;

import jakarta.validation.Valid;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import woohakdong.server.api.controller.group.dto.CreateOrderRequest;
import woohakdong.server.api.controller.group.dto.GroupIdResponse;
import woohakdong.server.api.controller.group.dto.GroupInfoResponse;
import woohakdong.server.api.controller.group.dto.GroupUpdateRequest;
import woohakdong.server.api.controller.group.dto.OrderIdResponse;
import woohakdong.server.api.controller.group.dto.PaymentCompleteReqeust;
import woohakdong.server.api.controller.group.dto.PortOneWebhookRequest;
import woohakdong.server.api.service.group.GroupService;
import woohakdong.server.api.service.order.OrderService;

@RestController
@RequestMapping("/v1/groups")
@RequiredArgsConstructor
public class GroupController implements GroupControllerDocs {

    private final OrderService orderService;
    private final GroupService groupService;

    @PostMapping("/{groupId}/orders")
    public OrderIdResponse createClubJoinOrder(@PathVariable Long groupId,
                                               @RequestBody CreateOrderRequest request) {
        return orderService.registerOrder(groupId, request);
    }

    @PostMapping("/{groupId}/orders/confirm")
    public void completeClubJoinOrder(@PathVariable Long groupId,
                                      @RequestBody PaymentCompleteReqeust request) {
        orderService.confirmOrderPayment(groupId, request, LocalDate.now());
    }

    @PostMapping("/payment/webhook")
    public void portOnePaymentComplete(@Valid @RequestBody PortOneWebhookRequest request) {
        orderService.portOnePaymentComplete(request, LocalDate.now());
    }

    @GetMapping("/{groupId}")
    public GroupInfoResponse getGroupInfo(@PathVariable Long groupId) {
        return groupService.findOneGroup(groupId, LocalDate.now());
    }

    @PutMapping("/{groupId}")
    public GroupIdResponse updateGroupInfo(@PathVariable Long groupId,
                                           @Valid @RequestBody GroupUpdateRequest groupUpdateRequest) {
        return groupService.updateGroupInfo(groupId, groupUpdateRequest, LocalDate.now());
    }

    @DeleteMapping("/{groupId}")
    public void deleteGroup(@PathVariable Long groupId) {
        groupService.deleteGroup(groupId, LocalDate.now());
    }

    @PostMapping("/{groupId}/availability")
    public void changeAvailabilityOfGroup(@PathVariable Long groupId) {
        groupService.changeAvailabilityOfGroup(groupId, LocalDate.now());
    }
}
