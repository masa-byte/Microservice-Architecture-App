package com.walcart.ordermicroservice.repositories;

import com.walcart.ordermicroservice.domain.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPaypalPaymentId(String paypalPaymentId);
}
