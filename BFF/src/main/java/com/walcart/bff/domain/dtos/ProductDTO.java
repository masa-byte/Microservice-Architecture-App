package com.walcart.bff.domain.dtos;

import com.walcart.bff.domain.enumerations.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
}
