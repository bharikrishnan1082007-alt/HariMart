package com.harimart.repository;

import com.harimart.entity.Order;
import com.harimart.enums.OrderStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Order> findByOrderNumber(String orderNumber);

    Optional<Order> findByIdAndUserId(Long id, Long userId);

    List<Order> findAllByOrderByCreatedAtDesc();

    long countByOrderStatus(OrderStatus orderStatus);

    @Query("SELECT COALESCE(SUM(o.finalAmount), 0) FROM Order o WHERE o.orderStatus <> 'CANCELLED'")
    BigDecimal calculateTotalRevenue();

    @Query("SELECT o FROM Order o ORDER BY o.createdAt DESC")
    List<Order> findRecentOrders(Pageable pageable);
}
