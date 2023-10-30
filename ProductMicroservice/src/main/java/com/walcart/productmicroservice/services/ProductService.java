package com.walcart.productmicroservice.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.DeliverCallback;
import com.walcart.productmicroservice.domain.dtos.CategoryDTO;
import com.walcart.productmicroservice.domain.dtos.ProductDTO;
import com.walcart.productmicroservice.domain.entities.Product;
import com.walcart.productmicroservice.domain.enumerations.ProductStatus;
import com.walcart.productmicroservice.repositories.ProductRepository;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private Connection connection = null;
    private Channel productChannel = null;
    private final static String PRODUCT_QUEUE_NAME = "products";
    private final String consumerName;
    private final ObjectMapper objectMapper;

    @Autowired
    public ProductService(ProductRepository productRepository) throws IOException, TimeoutException {
        this.consumerName = "product-microservice"; // + id for replication of microservice
        this.objectMapper = new ObjectMapper();
        this.productRepository = productRepository;
        this.configureConnection();
        this.createProductMessageBroker();
    }

    private void configureConnection() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");

        this.connection = factory.newConnection();
        this.productChannel = connection.createChannel();
        this.productChannel.queueDeclare(PRODUCT_QUEUE_NAME, true, false, false, null);
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
                CategoryDTO.mapToCategoryID(productDTO.getCategoryDTO())
                )
        );
    }

    private void createProductMessageBroker() throws IOException {
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            ProductDTO productDTO = objectMapper.readValue(message, ProductDTO.class);
            createProduct(productDTO);
        };
        productChannel.basicConsume(PRODUCT_QUEUE_NAME, true, deliverCallback, consumerTag -> {});
    }

    public Optional<Product> getProductById(long id) {
        return productRepository.findById(id);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product updateProduct(long id, ProductDTO updatedProductDTO) {
        System.out.println(updatedProductDTO);
        return productRepository.findById(id)
                .map(existingProduct -> {
                    if(updatedProductDTO.getName() != null)
                        existingProduct.setName(updatedProductDTO.getName());
                    if(updatedProductDTO.getDescription() != null)
                        existingProduct.setDescription(updatedProductDTO.getDescription());
                    if(updatedProductDTO.getPrice() != null)
                        existingProduct.setPrice(updatedProductDTO.getPrice());
                    if(updatedProductDTO.getBrand() != null)
                        existingProduct.setBrand(updatedProductDTO.getBrand());
                    if(updatedProductDTO.getQuantity() != null)
                        existingProduct.setQuantity(updatedProductDTO.getQuantity());
                    if(updatedProductDTO.getSalesCounter() != null)
                        existingProduct.setSalesCounter(updatedProductDTO.getSalesCounter());
                    if(updatedProductDTO.getCategoryDTO() != null)
                        existingProduct.setCategory(CategoryDTO.mapToCategoryID(updatedProductDTO.getCategoryDTO()));
                    if(updatedProductDTO.getStatus() != null)
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
