package woohakdong.server.domain.order;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    boolean existsByOrderMerchantUid(String orderMerchantUid);

}
