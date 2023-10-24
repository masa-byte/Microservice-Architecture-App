package com.walcart.ordermicroservice.domain.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "paypal_payment_id")
    private String paypalPaymentId;

    @OneToOne(mappedBy = "payment")
    private Order order;

    public Payment(String paypalPaymentId, Order order) {
        this.paypalPaymentId = paypalPaymentId;
        this.order = order;
    }
}
