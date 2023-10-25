package com.walcart.bff.controllers;

import com.walcart.bff.domain.dtos.CategoryDTO;
import com.walcart.bff.domain.dtos.CustomerDTO;
import com.walcart.bff.domain.dtos.ProductDTO;
import com.walcart.bff.services.CustomerMicroservice;
import com.walcart.bff.services.OrderMicroservice;
import com.walcart.bff.services.ProductMicroservice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/bff")
public class BFFAPIController {

    private final CustomerMicroservice customerMicroservice;
    private final ProductMicroservice productMicroservice;

    public BFFAPIController(CustomerMicroservice customerMicroservice, ProductMicroservice productMicroservice) {
        this.customerMicroservice = customerMicroservice;
        this.productMicroservice = productMicroservice;
    }

    //region POST
    @PostMapping("/customers")
    public ResponseEntity<CustomerDTO> createCustomer(@RequestBody CustomerDTO customerDTO) {
        try {
            CustomerDTO customer = customerMicroservice.createCustomer(customerDTO);
            return new ResponseEntity<>(customer, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/categories")
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody CategoryDTO categoryDTO) {
        try {
            CategoryDTO category = productMicroservice.createCategory(categoryDTO);
            return new ResponseEntity<>(category, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/products")
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO productDTO, @RequestParam("categoryId") long categoryId) {
        try {
            Optional<CategoryDTO> category = productMicroservice.getCategoryById(categoryId);
            if (category.isPresent()) {
                productDTO.setCategoryDTO(category.get());
                ProductDTO product = productMicroservice.createProduct(productDTO);
                return new ResponseEntity<>(product, HttpStatus.CREATED);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    //endregion

    //region GET BY
    @GetMapping("/customers/email/{email}")
    public ResponseEntity<CustomerDTO> getCustomerByEmail(@PathVariable("email") String email) {
        try {
            Optional<CustomerDTO> customer = customerMicroservice.getCustomerByEmail(email);
            return customer
                    .map(customerDTO -> new ResponseEntity<>(customerDTO, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/customers/{id}")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable("id") long id) {
        try {
            Optional<CustomerDTO> customer = customerMicroservice.getCustomerById(id);
            return customer
                    .map(customerDTO -> new ResponseEntity<>(customerDTO, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable("id") long id) {
        try {
            Optional<CategoryDTO> category = productMicroservice.getCategoryById(id);
            return category
                    .map(categoryDTO -> new ResponseEntity<>(categoryDTO, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable("id") long id) {
        try {
            Optional<ProductDTO> product = productMicroservice.getProductById(id);
            return product
                    .map(productDTO -> new ResponseEntity<>(productDTO, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    //endregion

    //region GET ALL
    @GetMapping("/customers")
    public ResponseEntity<Iterable<CustomerDTO>> getAllCustomers() {
        try {
            List<CustomerDTO> customers = customerMicroservice.getAllCustomers();
            return new ResponseEntity<>(customers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/categories")
    public ResponseEntity<Iterable<CategoryDTO>> getAllCategories() {
        try {
            List<CategoryDTO> categories = productMicroservice.getAllCategories();
            return new ResponseEntity<>(categories, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/products")
    public ResponseEntity<Iterable<ProductDTO>> getAllProducts() {
        try {
            List<ProductDTO> products = productMicroservice.getAllProducts();
            return new ResponseEntity<>(products, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    //endregion

    //region PUT
    @PutMapping("/customers/{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable("id") long id, @RequestBody CustomerDTO updatedCustomerDTO) {
        try {
            Optional<CustomerDTO> updatedCustomer = customerMicroservice.updateCustomer(id, updatedCustomerDTO);
            return updatedCustomer
                    .map(customerDTO -> new ResponseEntity<>(customerDTO, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable("id") long id, @RequestBody CategoryDTO updatedCategoryDTO) {
        try {
            Optional<CategoryDTO> updatedCategory = productMicroservice.updateCategory(id, updatedCategoryDTO);
            System.out.println("updatedCategory: " + updatedCategory);
            return updatedCategory
                    .map(categoryDTO -> new ResponseEntity<>(categoryDTO, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable("id") long id,
                                                    @RequestBody ProductDTO updatedProductDTO,
                                                    @RequestParam(value = "categoryId", required = false) Long categoryId)
    {
        try {
            if (categoryId == null) {
                Optional<ProductDTO> updatedProduct = productMicroservice.updateProduct(id, updatedProductDTO);
                return updatedProduct
                        .map(productDTO -> new ResponseEntity<>(productDTO, HttpStatus.OK))
                        .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
            }
            else {
                Optional<CategoryDTO> category = productMicroservice.getCategoryById(categoryId);
                if (category.isPresent()) {
                    updatedProductDTO.setCategoryDTO(category.get());
                    Optional<ProductDTO> updatedProduct = productMicroservice.updateProduct(id, updatedProductDTO);
                    return updatedProduct
                            .map(productDTO -> new ResponseEntity<>(productDTO, HttpStatus.OK))
                            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
                }
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    //endregion

    //region DELETE
    @DeleteMapping("/customers/{id}")
    public ResponseEntity<HttpStatus> deleteCustomer(@PathVariable("id") long id) {
        try {
            boolean deleted = customerMicroservice.deleteCustomer(id);
            return deleted
                    ? new ResponseEntity<>(HttpStatus.OK)
                    : new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<HttpStatus> deleteCategory(@PathVariable("id") long id) {
        try {
            boolean deleted = productMicroservice.deleteCategory(id);
            return deleted
                    ? new ResponseEntity<>(HttpStatus.OK)
                    : new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<HttpStatus> deleteProduct(@PathVariable("id") long id) {
        try {
            boolean deleted = productMicroservice.deleteProduct(id);
            return deleted
                    ? new ResponseEntity<>(HttpStatus.OK)
                    : new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    //endregion
}
