package com.walcart.productmicroservice.controllers;

import com.walcart.productmicroservice.domain.dtos.ProductDTO;
import com.walcart.productmicroservice.domain.entities.Product;
import com.walcart.productmicroservice.services.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping()
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO productDTO) {
        return new ResponseEntity<>(
                ProductDTO.mapToProductDTO(productService.createProduct(productDTO)),
                HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable("id") long id) {
        return productService.getProductById(id)
                .map(value -> new ResponseEntity<>(ProductDTO.mapToProductDTO(value), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping()
    public ResponseEntity<Iterable<ProductDTO>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        List<ProductDTO> productsDTOs = new ArrayList<>();
        for (Product product : products) {
            productsDTOs.add(ProductDTO.mapToProductDTO(product));
        }
        return new ResponseEntity<>(productsDTOs, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable("id") long id, @RequestBody ProductDTO updatedProductDTO) {
        Product updatedProduct = productService.updateProduct(id, updatedProductDTO);
        if(updatedProduct != null) {
            updatedProductDTO = ProductDTO.mapToProductDTO(updatedProduct);
            return new ResponseEntity<>(updatedProductDTO, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PutMapping("/sales/{id}")
    public ResponseEntity<ProductDTO> updateProductSalesCounter(@PathVariable("id") long id, @RequestBody int soldCounter) {
        Product updatedProduct = productService.updateProductSalesCounter(id, soldCounter);
        if(updatedProduct != null) {
            ProductDTO updatedProductDTO = ProductDTO.mapToProductDTO(updatedProduct);
            return new ResponseEntity<>(updatedProductDTO, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteProduct(@PathVariable("id") long id) {
        return productService.deleteProduct(id)
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
