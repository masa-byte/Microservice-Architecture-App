package com.walcart.productmicroservice.domain.entities;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.walcart.productmicroservice.domain.enumerations.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "brand", nullable = false)
    private String brand;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    @Column(name = "sales_counter", nullable = false)
    private Integer salesCounter;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "date_created", nullable = false)
    @CreationTimestamp
    private ZonedDateTime dateCreated;

    @Column(name = "last_updated", nullable = false)
    @UpdateTimestamp
    private ZonedDateTime lastUpdated;

    @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE)
    private List<Review> reviews = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    public Product(String name, String description, String brand, BigDecimal price, Integer salesCounter, ProductStatus status, Integer quantity, Category category) {
        this.name = name;
        this.description = description;
        this.brand = brand;
        this.price = price;
        this.salesCounter = salesCounter;
        this.status = status;
        this.quantity = quantity;
        this.category = category;
    }

    public Product(Long id, String name, String description, String brand, BigDecimal price, Integer salesCounter, ProductStatus status, Integer quantity, Category category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.brand = brand;
        this.price = price;
        this.salesCounter = salesCounter;
        this.status = status;
        this.quantity = quantity;
        this.category = category;
    }
}
