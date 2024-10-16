package woohakdong.server.api.controller.group;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import woohakdong.server.api.controller.group.dto.ClubJoinConfirmRequest;
import woohakdong.server.api.controller.group.dto.ClubJoinOrderRequest;
import woohakdong.server.api.controller.group.dto.ClubJoinOrderResponse;
import woohakdong.server.api.controller.group.dto.PortOneWebhookRequest;
import woohakdong.server.api.service.order.OrderService;

@RestController
@RequestMapping("/v1/groups")
@RequiredArgsConstructor
public class GroupController implements GroupControllerDocs {

    private final OrderService orderService;

    @PostMapping("/{groupId}/joins")
    public ClubJoinOrderResponse createClubJoinOrder(
            @PathVariable Long groupId,
            @RequestBody ClubJoinOrderRequest request) {
        return orderService.registerOrder(groupId, request);
    }

    @PostMapping("/{groupId}/joins/confirms")
    public void completeClubJoinOrder(
            @PathVariable Long groupId,
            @RequestBody ClubJoinConfirmRequest request) {
    }

    @PostMapping("/payment/webhook")
    public void portOnePaymentComplete(@RequestBody PortOneWebhookRequest request) {

    }
}
