package com.walcart.productmicroservice.services;

import com.walcart.productmicroservice.domain.dtos.CategoryDTO;
import com.walcart.productmicroservice.domain.dtos.ProductDTO;
import com.walcart.productmicroservice.domain.entities.Product;
import com.walcart.productmicroservice.domain.enumerations.ProductStatus;
import com.walcart.productmicroservice.repositories.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product createProduct(ProductDTO productDTO) {
        return productRepository.save(new Product(
                productDTO.getName(),
                productDTO.getDescription(),
                productDTO.getBrand(),
                productDTO.getPrice(),
                0,
                ProductStatus.AVAILABLE,
                productDTO.getQuantity(),
                CategoryDTO.mapToCategory(productDTO.getCategoryDTO()))
                );
    }

    public Optional<Product> getProductById(long id) {
        return productRepository.findById(id);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product updateProduct(long id, ProductDTO updatedProductDTO) {
        return productRepository.findById(id)
                .map(existingProduct -> {
                    existingProduct.setName(updatedProductDTO.getName());
                    existingProduct.setDescription(updatedProductDTO.getDescription());
                    existingProduct.setPrice(updatedProductDTO.getPrice());
                    existingProduct.setQuantity(updatedProductDTO.getQuantity());
                    existingProduct.setSalesCounter(updatedProductDTO.getSalesCounter());
                    existingProduct.setCategory(CategoryDTO.mapToCategory(updatedProductDTO.getCategoryDTO()));
                    existingProduct.setStatus(updatedProductDTO.getStatus());
                    return productRepository.save(existingProduct);
                })
                .orElse(null);
    }

    public boolean deleteProduct(long id) {
        return productRepository.findById(id)
                .map(existingProduct -> {
                    productRepository.delete(existingProduct);
                    return true;
                })
                .orElse(false);
    }
}
