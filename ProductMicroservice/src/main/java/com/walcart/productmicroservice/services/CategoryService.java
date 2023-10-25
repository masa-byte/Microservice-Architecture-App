package com.walcart.productmicroservice.services;

import com.walcart.productmicroservice.domain.dtos.CategoryDTO;
import com.walcart.productmicroservice.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.walcart.productmicroservice.domain.entities.Category;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category createCategory(CategoryDTO categoryDTO) {
        return categoryRepository.save(CategoryDTO.mapToCategoryNoID(categoryDTO));
    }

    public Optional<Category> getCategoryById(long id) {
        return categoryRepository.findById(id);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category updateCategory(long id, CategoryDTO updatedCategoryDTO) {
        return categoryRepository.findById(id)
                .map(existingCategory -> {
                    if(updatedCategoryDTO.getName() != null)
                        existingCategory.setName(updatedCategoryDTO.getName());
                    if(updatedCategoryDTO.getDescription() != null)
                        existingCategory.setDescription(updatedCategoryDTO.getDescription());
                    return categoryRepository.save(existingCategory);
                })
                .orElse(null);
    }

    public boolean deleteCategory(long id) {
        return categoryRepository.findById(id)
                .map(existingCategory -> {
                    categoryRepository.delete(existingCategory);
                    return true;
                })
                .orElse(false);
    }
}
