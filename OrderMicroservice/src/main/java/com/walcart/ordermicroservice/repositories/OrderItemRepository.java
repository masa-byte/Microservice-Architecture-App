package com.walcart.ordermicroservice.repositories;

import com.walcart.ordermicroservice.domain.entities.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
