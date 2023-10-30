package com.walcart.productmicroservice.domain.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.ZonedDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "date_created", nullable = false)
    @CreationTimestamp
    private ZonedDateTime dateCreated;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public Review(String title, String description, Integer rating, Long customerId, Product product) {
        this.title = title;
        this.description = description;
        this.rating = rating;
        this.customerId = customerId;
        this.product = product;
    }
}
