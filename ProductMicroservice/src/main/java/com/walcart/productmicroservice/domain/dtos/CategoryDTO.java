package com.walcart.productmicroservice.domain.dtos;

import com.walcart.productmicroservice.domain.entities.Category;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    private Long id;
    private String name;
    private String description;

    public static Category mapToCategoryID(CategoryDTO categoryDTO) {
        return new Category(categoryDTO.getId(), categoryDTO.getName(), categoryDTO.getDescription());
    }

    public static Category mapToCategoryNoID(CategoryDTO categoryDTO) {
        return new Category(categoryDTO.getName(), categoryDTO.getDescription());
    }

    public static CategoryDTO mapToCategoryDTO(Category category) {
        return new CategoryDTO(category.getId(), category.getName(), category.getDescription());
    }
}
