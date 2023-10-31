package com.walcart.ordermicroservice.repositories;

import com.walcart.ordermicroservice.domain.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o JOIN o.items oi " +
            "WHERE o.customerId = :customerId AND oi.productId = :productId")
    Optional<Order> findByCustomerIdAndProductId(Long customerId, Long productId);
}
