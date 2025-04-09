package woohakdong.server.domain.order;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface OrderJpaRepository extends JpaRepository<Order, Long> {
    boolean existsByOrderMerchantUid(String orderMerchantUid);

    Optional<Order> findByOrderMerchantUid(String orderMerchantUid);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Order o WHERE o.orderId = :orderId")
    Optional<Order> findByOrderIdWithLock(Long orderId);
}
