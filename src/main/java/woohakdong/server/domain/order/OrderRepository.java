package woohakdong.server.domain.order;

public interface OrderRepository {
    Order save(Order order);

    Order getByIdWithLock(Long orderId);

    boolean existsByOrderMerchantUid(String orderMerchantUid);

    Order getByOrderMerchantUid(String orderMerchantUid);
}
