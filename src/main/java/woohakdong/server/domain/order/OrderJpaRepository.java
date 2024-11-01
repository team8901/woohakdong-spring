package woohakdong.server.domain.order;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository extends JpaRepository<Order, Long> {
    boolean existsByOrderMerchantUid(String orderMerchantUid);

    Optional<Order> findByOrderMerchantUid(String orderMerchantUid);
}
