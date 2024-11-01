package woohakdong.server.domain.order;

import static woohakdong.server.common.exception.CustomErrorInfo.ORDER_NOT_FOUND;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import woohakdong.server.common.exception.CustomErrorInfo;
import woohakdong.server.common.exception.CustomException;

@RequiredArgsConstructor
@Repository
public class OrderRepositoryImpl implements OrderRepository{
    private final OrderJpaRepository orderJpaRepository;

    @Override
    public Order save(Order order) {
        return orderJpaRepository.save(order);
    }

    @Override
    public Order getById(Long orderId) {
        return orderJpaRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(ORDER_NOT_FOUND));
    }

    @Override
    public boolean existsByOrderMerchantUid(String orderMerchantUid) {
        return orderJpaRepository.existsByOrderMerchantUid(orderMerchantUid);
    }

    @Override
    public Order getByOrderMerchantUid(String orderMerchantUid) {
        return orderJpaRepository.findByOrderMerchantUid(orderMerchantUid)
                .orElseThrow(() -> new CustomException(ORDER_NOT_FOUND));
    }
}
