package com.walcart.bff.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.walcart.bff.domain.dtos.CategoryDTO;
import com.walcart.bff.domain.dtos.ProductDTO;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.walcart.bff.domain.dtos.ReviewDTO;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

import static com.walcart.bff.Endpoints.productsMicroserviceApiUrl;

@Service
public class ProductMicroservice {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String categoryApiUrl = productsMicroserviceApiUrl + "/categories";
    private final String productApiUrl = productsMicroserviceApiUrl + "/products";
    private final String reviewApiUrl = productsMicroserviceApiUrl + "/reviews";

    private static Connection connection = null;
    private static Channel productChannel = null;
    private static Channel reviewChannel = null;
    private static final String PRODUCT_QUEUE_NAME = "products";
    private static final String REVIEW_QUEUE_NAME = "reviews";

    public ProductMicroservice() throws IOException, TimeoutException {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.configureConnection();
    }

    private void configureConnection() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");

        connection = factory.newConnection();

        productChannel = connection.createChannel();
        productChannel.queueDeclare(PRODUCT_QUEUE_NAME, true, false, false, null);
        reviewChannel = connection.createChannel();
        reviewChannel.queueDeclare(REVIEW_QUEUE_NAME, true, false, false, null);
    }

    public CategoryDTO createCategory(CategoryDTO categoryDTO) throws IOException, InterruptedException {
        String url = categoryApiUrl;
        URI uri = URI.create(url);
        String requestBody = objectMapper.writeValueAsString(categoryDTO);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        if (statusCode == 201) {
            String responseBody = response.body();
            CategoryDTO createdCategoryDTO = objectMapper.readValue(responseBody, CategoryDTO.class);
            return createdCategoryDTO;
        } else {
            return null;
        }
    }

    public ProductDTO createProduct(ProductDTO productDTO) throws IOException, InterruptedException {
        String url = productApiUrl;
        URI uri = URI.create(url);
        String requestBody = objectMapper.writeValueAsString(productDTO);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        if (statusCode == 201) {
            String responseBody = response.body();
            ProductDTO createdProductDTO = objectMapper.readValue(responseBody, ProductDTO.class);
            return createdProductDTO;
        } else {
            return null;
        }
    }

    public ReviewDTO createReview(ReviewDTO reviewDTO) throws IOException, InterruptedException {
        String url = reviewApiUrl;
        URI uri = URI.create(url);
        String requestBody = objectMapper.writeValueAsString(reviewDTO);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        System.out.println(statusCode);
        if (statusCode == 201) {
            String responseBody = response.body();
            ReviewDTO createdReviewDTO = objectMapper.readValue(responseBody, ReviewDTO.class);
            return createdReviewDTO;
        } else {
            return null;
        }
    }

    public void createProductMessageBroker(ProductDTO productDTO) throws IOException {
        String message = objectMapper.writeValueAsString(productDTO);
        productChannel.basicPublish("", PRODUCT_QUEUE_NAME, null, message.getBytes());
    }

    public void createReviewMessageBroker(ReviewDTO reviewDTO) throws IOException {
        String message = objectMapper.writeValueAsString(reviewDTO);
        productChannel.basicPublish("", REVIEW_QUEUE_NAME, null, message.getBytes());
    }

    public Optional<CategoryDTO> getCategoryById(long id) throws IOException, InterruptedException {
        String url = categoryApiUrl + "/" + id;
        URI uri = URI.create(url);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        if (statusCode == 200) {
            String responseBody = response.body();
            CategoryDTO categoryDTO = objectMapper.readValue(responseBody, CategoryDTO.class);
            return Optional.of(categoryDTO);
        } else {
            return Optional.empty();
        }
    }

    public Optional<ProductDTO> getProductById(long id) throws IOException, InterruptedException {
        String url = productApiUrl + "/" + id;
        URI uri = URI.create(url);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        if (statusCode == 200) {
            String responseBody = response.body();
            ProductDTO productDTO = objectMapper.readValue(responseBody, ProductDTO.class);
            return Optional.of(productDTO);
        } else {
            return Optional.empty();
        }
    }

    public Optional<ReviewDTO> getReviewById(long id) throws IOException, InterruptedException {
        String url = reviewApiUrl + "/" + id;
        URI uri = URI.create(url);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        System.out.println(statusCode);
        if (statusCode == 200) {
            String responseBody = response.body();
            ReviewDTO reviewDTO = objectMapper.readValue(responseBody, ReviewDTO.class);
            return Optional.of(reviewDTO);
        } else {
            return Optional.empty();
        }
    }

    public List<CategoryDTO> getAllCategories() throws IOException, InterruptedException {
        String url = categoryApiUrl;
        URI uri = URI.create(url);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        if (statusCode == 200) {
            String responseBody = response.body();
            CategoryDTO[] categoryDTOs = objectMapper.readValue(responseBody, CategoryDTO[].class);
            return List.of(categoryDTOs);
        } else {
            return null;
        }
    }

    public List<ProductDTO> getAllProducts() throws IOException, InterruptedException {
        String url = productApiUrl;
        URI uri = URI.create(url);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        if (statusCode == 200) {
            String responseBody = response.body();
            ProductDTO[] productDTOs = objectMapper.readValue(responseBody, ProductDTO[].class);
            return List.of(productDTOs);
        } else {
            return null;
        }
    }

    public List<ReviewDTO> getAllReviews() throws IOException, InterruptedException {
        String url = reviewApiUrl;
        URI uri = URI.create(url);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        System.out.println(statusCode);
        if (statusCode == 200) {
            String responseBody = response.body();
            ReviewDTO[] reviewDTOs = objectMapper.readValue(responseBody, ReviewDTO[].class);
            return List.of(reviewDTOs);
        } else {
            return null;
        }
    }

    public Optional<CategoryDTO> updateCategory(long id, CategoryDTO updatedCategoryDTO) throws IOException, InterruptedException {
        String url = categoryApiUrl + "/" + id;
        URI uri = URI.create(url);
        String requestBody = objectMapper.writeValueAsString(updatedCategoryDTO);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        if (statusCode == 200) {
            String responseBody = response.body();
            CategoryDTO categoryDTO = objectMapper.readValue(responseBody, CategoryDTO.class);
            return Optional.of(categoryDTO);
        } else {
            return null;
        }
    }

    public Optional<ProductDTO> updateProduct(long id, ProductDTO updatedProductDTO) throws IOException, InterruptedException {
        String url = productApiUrl + "/" + id;
        URI uri = URI.create(url);
        String requestBody = objectMapper.writeValueAsString(updatedProductDTO);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        if (statusCode == 200) {
            String responseBody = response.body();
            ProductDTO productDTO = objectMapper.readValue(responseBody, ProductDTO.class);
            return Optional.of(productDTO);
        } else {
            return null;
        }
    }

    public boolean deleteCategory(long id) throws IOException, InterruptedException {
        String url = categoryApiUrl + "/" + id;
        URI uri = URI.create(url);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        return statusCode == 200;
    }

    public boolean deleteProduct(long id) throws IOException, InterruptedException {
        String url = productApiUrl + "/" + id;
        URI uri = URI.create(url);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        return statusCode == 200;
    }

    public boolean deleteReview(long id) throws IOException, InterruptedException {
        String url = reviewApiUrl + "/" + id;
        URI uri = URI.create(url);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        return statusCode == 200;
    }
}
