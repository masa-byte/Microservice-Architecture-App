package com.walcart.bff.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.walcart.bff.domain.dtos.OrderDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

import static com.walcart.bff.Endpoints.ordersMicroserviceApiUrl;

@Service
public class OrderMicroservice {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String ordersApiUrl = ordersMicroserviceApiUrl + "/orders";
    private final String orderItemsApiUrl = ordersMicroserviceApiUrl + "/orderItems";
    private final String paymentsApiUrl = ordersMicroserviceApiUrl + "/payments";

    private Connection connection = null;
    private Channel orderItemChannel = null;
    private final String ORDER_ITEMS_QUEUE_NAME = "order-items";
    @Autowired
    public OrderMicroservice() throws IOException, TimeoutException {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.configureConnection();
    }

    private void configureConnection() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");

        connection = factory.newConnection();

        orderItemChannel = connection.createChannel();
        orderItemChannel.queueDeclare(ORDER_ITEMS_QUEUE_NAME, true, false, false, null);
    }

    public OrderDTO createOrder(OrderDTO orderDTO) throws IOException, InterruptedException {
        String url = ordersApiUrl;
        URI uri = URI.create(url);
        String requestBody = objectMapper.writeValueAsString(orderDTO);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        if (statusCode == 201) {
            String responseBody = response.body();
            OrderDTO createdOrderDTO = objectMapper.readValue(responseBody, OrderDTO.class);
            return createdOrderDTO;
        } else {
            return null;
        }
    }

    public Optional<OrderDTO> getOrderById(long id) throws IOException, InterruptedException {
        String url = ordersApiUrl + "/" + id;
        URI uri = URI.create(url);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        if (statusCode == 200) {
            String responseBody = response.body();
            OrderDTO orderDTO = objectMapper.readValue(responseBody, OrderDTO.class);
            return Optional.of(orderDTO);
        } else {
            return Optional.empty();
        }
    }

    public Optional<OrderDTO> getOrderByCustomerIdAndProductId(long customerId, long productId) throws IOException, InterruptedException {
        String url = ordersApiUrl + "/customer/" + customerId + "/product/" + productId;
        URI uri = URI.create(url);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        if (statusCode == 200) {
            String responseBody = response.body();
            OrderDTO orderDTO = objectMapper.readValue(responseBody, OrderDTO.class);
            return Optional.of(orderDTO);
        } else {
            return Optional.empty();
        }
    }

    public List<OrderDTO> getAllOrders() throws IOException, InterruptedException {
        String url = ordersApiUrl;
        URI uri = URI.create(url);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        if (statusCode == 200) {
            String responseBody = response.body();
            OrderDTO[] orderDTOs = objectMapper.readValue(responseBody, OrderDTO[].class);
            return List.of(orderDTOs);
        } else {
            return null;
        }
    }

    public Optional<OrderDTO> updateOrder(long id, OrderDTO updatedOrderDTO) throws IOException, InterruptedException {
        String url = ordersApiUrl + "/" + id;
        URI uri = URI.create(url);
        String requestBody = objectMapper.writeValueAsString(updatedOrderDTO);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        if (statusCode == 200) {
            String responseBody = response.body();
            OrderDTO orderDTO = objectMapper.readValue(responseBody, OrderDTO.class);
            return Optional.of(orderDTO);
        } else {
            return Optional.empty();
        }
    }
    public void updateRatedStatus(long id, boolean status) throws IOException, InterruptedException {
        String url = orderItemsApiUrl + "/status/" + id;
        URI uri = URI.create(url);
        String requestBody = objectMapper.writeValueAsString(status);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public void updateRatedStatusMessageBroker(long id, boolean status) throws IOException, InterruptedException {
        Object DTO = new Object() {
            public final long _id = id;
            public final boolean _status = status;
        };
        String message = objectMapper.writeValueAsString(DTO);
        orderItemChannel.basicPublish("", ORDER_ITEMS_QUEUE_NAME, null, message.getBytes());
    }

    public boolean deleteOrder(long id) throws IOException, InterruptedException {
        String url = ordersApiUrl + "/" + id;
        URI uri = URI.create(url);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        return statusCode == 204;
    }
}
