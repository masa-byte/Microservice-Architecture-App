package com.walcart.ordermicroservice.domain.entities;

import com.walcart.ordermicroservice.domain.enumerations.OrderStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "total_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalPrice;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "date_created", nullable = false)
    @CreationTimestamp
    private ZonedDateTime dateCreated;

    @Column(name = "last_updated", nullable = false)
    @UpdateTimestamp
    private ZonedDateTime lastUpdated;

    @Column(name = "shipped")
    private ZonedDateTime shipped;

    @Embedded
    private Address shipmentAddress;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "payment_id", referencedColumnName = "id", unique = true)
    private Payment payment;

    @OneToMany(mappedBy = "order", cascade = CascadeType.REMOVE)
    private List<OrderItem> items = new ArrayList<>();

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    public Order(BigDecimal totalPrice, OrderStatus status, ZonedDateTime shipped, Address shipmentAddress, Payment payment, List<OrderItem> items, Long customerId) {
        this.totalPrice = totalPrice;
        this.status = status;
        this.shipped = shipped;
        this.shipmentAddress = shipmentAddress;
        this.items = items;
        this.payment = payment;
        this.customerId = customerId;
    }
}
