package woohakdong.server.api.service.group;

import static woohakdong.server.common.exception.CustomErrorInfo.GROUP_NOT_FIND;
import static woohakdong.server.common.exception.CustomErrorInfo.MEMBER_NOT_FOUND;
import static woohakdong.server.common.exception.CustomErrorInfo.ORDER_ALREADY_EXIST;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woohakdong.server.api.controller.group.dto.ClubJoinOrderRequest;
import woohakdong.server.api.controller.group.dto.ClubJoinOrderResponse;
import woohakdong.server.common.exception.CustomException;
import woohakdong.server.common.security.jwt.CustomUserDetails;
import woohakdong.server.domain.clubmember.ClubMemberRepository;
import woohakdong.server.domain.group.Group;
import woohakdong.server.domain.group.GroupRepository;
import woohakdong.server.domain.member.Member;
import woohakdong.server.domain.member.MemberRepository;
import woohakdong.server.domain.order.Order;
import woohakdong.server.domain.order.OrderRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupService {

    private final MemberRepository memberRepository;
    private final ClubMemberRepository clubMemberRepository;
    private final GroupRepository groupRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public ClubJoinOrderResponse registerOrder(Long groupId, ClubJoinOrderRequest request) {
        Member member = getMemberFromJwtInformation();

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new CustomException(GROUP_NOT_FIND));

        if ( orderRepository.existsByOrderMerchantUid(request.merchantUid()) ) {
            throw new CustomException(ORDER_ALREADY_EXIST);
        }

        switch ( group.getGroupType() )
        {
            case JOIN:
                // TODO : member가 clubMember에 속해있는지 확인
                // clubMemberRepository.findByClubAndMember(club, member)
                //       .orElseThrow(() -> new CustomException(CLUB_NOT_FOUND));
                break;
            case EVENT:
                // TODO : member가 clubGroupMember에 속해있는지 확인
                // clubGroupMemberRepository.findByClubAndMember(club, member)
                //       .orElseThrow(() -> new CustomException(CLUB_NOT_FOUND));
            default:
                // TODO : throw exception
                //throw new CustomException(GROUP_TYPE_NOT_FOUND);
                break;
        }

        Order order = createOrder(request, group, member);
        orderRepository.save(order);

        return ClubJoinOrderResponse.builder()
                .orderId(order.getId())
                .build();
    }

    private static Order createOrder(ClubJoinOrderRequest request, Group group, Member member) {
        return Order.builder()
                .group(group)
                .member(member)
                .orderAt(LocalDateTime.now())
                .orderAmount(group.getGroupAmount())
                .orderMerchantUid(request.merchantUid())
                .build();
    }

    private Member getMemberFromJwtInformation() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String provideId = userDetails.getUsername();

        return memberRepository.findByMemberProvideId(provideId)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
    }
}
