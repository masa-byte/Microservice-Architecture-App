package com.walcart.productmicroservice.domain.dtos;

import com.walcart.productmicroservice.domain.entities.Product;
import com.walcart.productmicroservice.domain.enumerations.ProductStatus;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private String brand;
    private BigDecimal price;
    private Integer salesCounter;
    private ProductStatus status;
    private Integer quantity;
    private CategoryDTO categoryDTO;

    public static Product mapToProduct(ProductDTO productDTO) {
        return new Product(
                productDTO.getName(),
                productDTO.getDescription(),
                productDTO.getBrand(),
                productDTO.getPrice(),
                productDTO.getSalesCounter(),
                productDTO.getStatus(),
                productDTO.getQuantity(),
                CategoryDTO.mapToCategory(productDTO.getCategoryDTO()));
    }

    public static ProductDTO mapToProductDTO(Product product) {
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getBrand(),
                product.getPrice(),
                product.getSalesCounter(),
                product.getStatus(),
                product.getQuantity(),
                CategoryDTO.mapToCategoryDTO(product.getCategory()));
    }
}
