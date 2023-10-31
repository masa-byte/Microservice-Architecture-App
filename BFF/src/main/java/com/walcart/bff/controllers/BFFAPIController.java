package com.walcart.bff.controllers;

import com.walcart.bff.domain.dtos.*;
import com.walcart.bff.domain.enumerations.OrderStatus;
import com.walcart.bff.domain.wrapper.CreateOrderRequest;
import com.walcart.bff.services.CustomerMicroservice;
import com.walcart.bff.services.OrderMicroservice;
import com.walcart.bff.services.ProductMicroservice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/bff")
public class BFFAPIController {

    private final CustomerMicroservice customerMicroservice;
    private final ProductMicroservice productMicroservice;
    private final OrderMicroservice orderMicroservice;

    public BFFAPIController(CustomerMicroservice customerMicroservice, ProductMicroservice productMicroservice, OrderMicroservice orderMicroservice) {
        this.customerMicroservice = customerMicroservice;
        this.productMicroservice = productMicroservice;
        this.orderMicroservice = orderMicroservice;
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

    @PostMapping("/reviews")
    public ResponseEntity<ReviewDTO> createReview(
            @RequestBody ReviewDTO reviewDTO,
            @RequestParam("productId") long productId,
            @RequestParam("customerId") long customerId
    ) {
        try {
            Optional<ProductDTO> product = productMicroservice.getProductById(productId);
            Optional<CustomerDTO> customer = customerMicroservice.getCustomerById(customerId);
            if (product.isPresent() && customer.isPresent()) {
                Optional<OrderDTO> checkOrder = orderMicroservice.getOrderByCustomerIdAndProductId(customerId, productId);
                if(checkOrder.isPresent() && checkOrder.get().getStatus() == OrderStatus.DELIVERED) {
                    reviewDTO.setProductDTO(product.get());
                    reviewDTO.setCustomerId(customerId);
                    ReviewDTO review = productMicroservice.createReview(reviewDTO);
                    orderMicroservice.updateRatedStatus(productId, true);
                    return new ResponseEntity<>(review, HttpStatus.CREATED);
                }
                else
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/orders")
    public ResponseEntity<OrderDTO> createOrder(
            @RequestBody CreateOrderRequest request,
            @RequestParam("customerId") long customerId,
            @RequestParam("paypalPaymentId") String paypalPaymentId
    ) {
        try {
            OrderDTO orderDTO = request.getOrderDTO();
            List<OrderItemDTO> orderItemsDTO = request.getOrderItemsDTO();
            PaymentDTO paymentDTO = new PaymentDTO(paypalPaymentId);
            BigDecimal totalPrice = new BigDecimal(0);
            for (OrderItemDTO orderItemDTO : orderItemsDTO) {
                Optional<ProductDTO> product = productMicroservice.getProductById(orderItemDTO.getProductId());
                if (product.isEmpty()) {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
                totalPrice = totalPrice.add(product.get().getPrice().multiply(new BigDecimal(orderItemDTO.getQuantity())));
            }
            Optional<CustomerDTO> customer = customerMicroservice.getCustomerById(customerId);
            if (customer.isPresent()) {
                orderDTO.setCustomerId(customerId);
                orderDTO.setPaymentDTO(paymentDTO);
                orderDTO.setItems(orderItemsDTO);
                orderDTO.setTotalPrice(totalPrice);
                OrderDTO order = orderMicroservice.createOrder(orderDTO);
                if(order != null) {
                    for (OrderItemDTO orderItemDTO : orderItemsDTO) {
                        Optional<ProductDTO> product = productMicroservice.getProductById(orderItemDTO.getProductId());
                        productMicroservice.updateProductSalesCounter(product.get().getId(), orderItemDTO.getQuantity());
                    }
                }
                return new ResponseEntity<>(order, HttpStatus.CREATED);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/products/message")
    public ResponseEntity<HttpStatus> createProductMessage(@RequestBody ProductDTO productDTO, @RequestParam("categoryId") long categoryId) {
        try {
            Optional<CategoryDTO> category = productMicroservice.getCategoryById(categoryId);
            if (category.isPresent()) {
                productDTO.setCategoryDTO(category.get());
                productMicroservice.createProductMessageBroker(productDTO);
                return new ResponseEntity<>(HttpStatus.CREATED);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/reviews/message")
    public ResponseEntity<HttpStatus> createReviewMessage(
            @RequestBody ReviewDTO reviewDTO,
            @RequestParam("productId") long productId,
            @RequestParam("customerId") long customerId
            ) {
        try {
            Optional<ProductDTO> product = productMicroservice.getProductById(productId);
            Optional<CustomerDTO> customer = customerMicroservice.getCustomerById(customerId);
            if (product.isPresent() && customer.isPresent()) {
                Optional<OrderDTO> checkOrder = orderMicroservice.getOrderByCustomerIdAndProductId(customerId, productId);
                reviewDTO.setProductDTO(product.get());
                reviewDTO.setCustomerId(customer.get().getId());
                productMicroservice.createReviewMessageBroker(reviewDTO);
                orderMicroservice.updateRatedStatusMessageBroker(productId, true);
                return new ResponseEntity<>(HttpStatus.CREATED);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/orders/message")
    public ResponseEntity<HttpStatus> createOrderMessage(
            @RequestBody CreateOrderRequest request,
            @RequestParam("customerId") long customerId,
            @RequestParam("paypalPaymentId") String paypalPaymentId
    ) {
        try {
            OrderDTO orderDTO = request.getOrderDTO();
            List<OrderItemDTO> orderItemsDTO = request.getOrderItemsDTO();
            PaymentDTO paymentDTO = new PaymentDTO(paypalPaymentId);
            BigDecimal totalPrice = new BigDecimal(0);
            for (OrderItemDTO orderItemDTO : orderItemsDTO) {
                Optional<ProductDTO> product = productMicroservice.getProductById(orderItemDTO.getProductId());
                if (product.isEmpty()) {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
                totalPrice = totalPrice.add(product.get().getPrice().multiply(new BigDecimal(orderItemDTO.getQuantity())));
            }
            Optional<CustomerDTO> customer = customerMicroservice.getCustomerById(customerId);
            if (customer.isPresent()) {
                orderDTO.setCustomerId(customerId);
                orderDTO.setPaymentDTO(paymentDTO);
                orderDTO.setItems(orderItemsDTO);
                orderDTO.setTotalPrice(totalPrice);
                orderMicroservice.createOrderMessageBroker(orderDTO);
                return new ResponseEntity<>(HttpStatus.CREATED);
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

    @GetMapping("/reviews/{id}")
    public ResponseEntity<ReviewDTO> getReviewById(@PathVariable("id") long id) {
        try {
            Optional<ReviewDTO> review = productMicroservice.getReviewById(id);
            return review
                    .map(reviewDTO -> new ResponseEntity<>(reviewDTO, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable("id") long id) {
        try {
            Optional<OrderDTO> order = orderMicroservice.getOrderById(id);
            return order
                    .map(orderDTO -> new ResponseEntity<>(orderDTO, HttpStatus.OK))
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

    @GetMapping("/reviews")
    public ResponseEntity<Iterable<ReviewDTO>> getAllReviews() {
        try {
            List<ReviewDTO> reviews = productMicroservice.getAllReviews();
            return new ResponseEntity<>(reviews, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/orders")
    public ResponseEntity<Iterable<OrderDTO>> getAllOrders() {
        try {
            List<OrderDTO> orders = orderMicroservice.getAllOrders();
            return new ResponseEntity<>(orders, HttpStatus.OK);
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

    @PutMapping("/orders/{id}")
    public ResponseEntity<OrderDTO> updateOrder(@PathVariable("id") long id, @RequestBody OrderDTO updatedOrderDTO) {
        try {
            Optional<OrderDTO> updatedOrder = orderMicroservice.updateOrder(id, updatedOrderDTO);
            if(updatedOrder.isPresent() && updatedOrderDTO.getStatus() == OrderStatus.CANCELLED) {
                for (OrderItemDTO orderItemDTO : updatedOrder.get().getItems()) {
                    Optional<ProductDTO> product = productMicroservice.getProductById(orderItemDTO.getProductId());
                    productMicroservice.updateProductSalesCounter(product.get().getId(), -orderItemDTO.getQuantity());
                }
            }
            return updatedOrder
                    .map(orderDTO -> new ResponseEntity<>(orderDTO, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
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

    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<HttpStatus> deleteReview(@PathVariable("id") long id) {
        try {
            Optional<ReviewDTO> review = productMicroservice.getReviewById(id);
            if(review.isPresent()) {
                ProductDTO product = review.get().getProductDTO();
                orderMicroservice.updateRatedStatus(product.getId(), false);
            }
            boolean deleted = productMicroservice.deleteReview(id);
            return deleted
                    ? new ResponseEntity<>(HttpStatus.OK)
                    : new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/orders/{id}")
    public ResponseEntity<HttpStatus> deleteOrder(@PathVariable("id") long id) {
        try {
            Optional<OrderDTO> order = orderMicroservice.getOrderById(id);
            if(order.isPresent())  {
                if(order.get().getStatus() != OrderStatus.CANCELLED) {
                    for (OrderItemDTO orderItemDTO : order.get().getItems()) {
                        Optional<ProductDTO> product = productMicroservice.getProductById(orderItemDTO.getProductId());
                        productMicroservice.updateProductSalesCounter(product.get().getId(), -orderItemDTO.getQuantity());
                    }
                }
                boolean deleted = orderMicroservice.deleteOrder(id);
                return deleted
                        ? new ResponseEntity<>(HttpStatus.OK)
                        : new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            else
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    //endregion
}
