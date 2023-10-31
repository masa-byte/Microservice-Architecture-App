package com.walcart.ordermicroservice.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "rated", columnDefinition = "boolean default false")
    private Boolean rated;

    public OrderItem(Integer quantity, Long productId, Boolean rated) {
        this.quantity = quantity;
        this.productId = productId;
        this.rated = rated;
    }
}
