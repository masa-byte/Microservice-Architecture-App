package com.walcart.ordermicroservice.repositories;

import com.walcart.ordermicroservice.domain.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
