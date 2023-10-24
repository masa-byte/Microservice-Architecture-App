package com.walcart.productmicroservice.controllers;

import com.walcart.productmicroservice.domain.dtos.CategoryDTO;
import com.walcart.productmicroservice.domain.entities.Category;
import com.walcart.productmicroservice.services.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping()
    public ResponseEntity<CategoryDTO> createCategory(CategoryDTO categoryDTO) {
        return new ResponseEntity<>(
                CategoryDTO.mapToCategoryDTO(categoryService.createCategory(categoryDTO)),
                HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable("id") long id) {
        return categoryService.getCategoryById(id)
                .map(value -> new ResponseEntity<>(CategoryDTO.mapToCategoryDTO(value), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping()
    public ResponseEntity<Iterable<CategoryDTO>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        List<CategoryDTO> categoryDTOs = new ArrayList<>();
        for (Category category : categories) {
            categoryDTOs.add(CategoryDTO.mapToCategoryDTO(category));
        }
        return new ResponseEntity<>(categoryDTOs, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable("id") long id, @RequestBody CategoryDTO updatedCategoryDTO) {
        Category updatedCategory = categoryService.updateCategory(id, updatedCategoryDTO);
        if (updatedCategory != null) {
            updatedCategoryDTO = CategoryDTO.mapToCategoryDTO(updatedCategory);
            return new ResponseEntity<>(updatedCategoryDTO, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteCategory(@PathVariable("id") long id) {
        return categoryService.deleteCategory(id)
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
