package com.walcart.ordermicroservice.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.walcart.ordermicroservice.domain.dtos.*;
import com.walcart.ordermicroservice.domain.entities.Order;
import com.walcart.ordermicroservice.domain.entities.OrderItem;
import com.walcart.ordermicroservice.domain.entities.Payment;
import com.walcart.ordermicroservice.domain.enumerations.OrderStatus;
import com.walcart.ordermicroservice.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private Connection connection = null;
    private Channel createOrderChannel = null;
    private Channel updateProductSalesChannel = null;
    private final static String ORDERS_QUEUE_NAME = "orders";
    private final static String PRODUCT_QUEUE_NAME = "update-products";
    private final String consumerName;
    private final ObjectMapper objectMapper;

    @Autowired
    public OrderService(OrderRepository orderRepository) throws IOException, TimeoutException {
        this.consumerName = "order-microservice"; // + id for replication of microservice
        this.orderRepository = orderRepository;
        this.objectMapper = new ObjectMapper();
        this.configureConnection();
        this.createOrderMessageBroker();
    }

    private void configureConnection() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");

        this.connection = factory.newConnection();

        this.createOrderChannel = connection.createChannel();
        this.createOrderChannel.queueDeclare(ORDERS_QUEUE_NAME, true, false, false, null);
        this.updateProductSalesChannel = connection.createChannel();
        this.updateProductSalesChannel.queueDeclare(PRODUCT_QUEUE_NAME, true, false, false, null);
    }

    public Order createOrder(OrderDTO orderDTO) {
        Order order = new Order();
        order.setShipped(null);
        order.setStatus(OrderStatus.CREATED);
        order.setTotalPrice(orderDTO.getTotalPrice());
        order.setShipmentAddress(orderDTO.getShipmentAddress());
        order.setCustomerId(orderDTO.getCustomerId());

        Payment payment = new Payment();
        payment.setPaypalPaymentId(orderDTO.getPaymentDTO().getPaypalPaymentId());
        order.setPayment(payment);
        payment.setOrder(order);

        List<OrderItemDTO> orderItemsDTOs = orderDTO.getItems();
        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemDTO orderItemDTO : orderItemsDTOs) {
            OrderItem orderItem = new OrderItem();
            orderItem.setQuantity(orderItemDTO.getQuantity());
            orderItem.setProductId(orderItemDTO.getProductId());
            orderItem.setOrder(order);
            orderItems.add(orderItem);
        }
        order.setItems(orderItems);

        return orderRepository.save(order);
    }

    private void createOrderMessageBroker() throws IOException {
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            OrderDTO orderDTO = objectMapper.readValue(message, OrderDTO.class);
            createOrder(orderDTO);
            this.updateProductQuantityMessageBroker(orderDTO.getItems());
        };
        createOrderChannel.basicConsume(ORDERS_QUEUE_NAME, true, deliverCallback, consumerTag -> {});
    }

    private void updateProductQuantityMessageBroker(List<OrderItemDTO> orderItemsDTOs) throws IOException {
        for (OrderItemDTO orderItemDTO : orderItemsDTOs) {
            String message = objectMapper.writeValueAsString(orderItemDTO);
            updateProductSalesChannel.basicPublish("", PRODUCT_QUEUE_NAME, null, message.getBytes());
        }
    }

    public Optional<Order> getOrderById(long id) {
        return orderRepository.findById(id);
    }

    public Optional<Order> getOrderByCustomerIdAndProductId(long customerId, long productId) {
        return orderRepository.findByCustomerIdAndProductId(customerId, productId);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order updateOrder(long id, OrderDTO updatedOrderDTO) {
        ZoneId zoneId = ZoneId.of("America/New_York");
        ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);
        return switch (updatedOrderDTO.getStatus()) {
            case CANCELLED -> orderRepository.findById(id)
                    .map(existingOrder -> {
                        existingOrder.setStatus(updatedOrderDTO.getStatus());
                        return orderRepository.save(existingOrder);
                    })
                    .orElse(null);
            case SHIPPED -> orderRepository.findById(id)
                    .map(existingOrder -> {
                        existingOrder.setStatus(updatedOrderDTO.getStatus());
                        existingOrder.setShipped(zonedDateTime);
                        return orderRepository.save(existingOrder);
                    })
                    .orElse(null);
            case DELIVERED -> orderRepository.findById(id)
                    .map(existingOrder -> {
                        existingOrder.setStatus(updatedOrderDTO.getStatus());
                        existingOrder.setDelivered(zonedDateTime);
                        return orderRepository.save(existingOrder);
                    })
                    .orElse(null);
            default -> null;
        };
    }

    public boolean deleteOrder(long id) {
        return orderRepository.findById(id)
                .map(existingOrder -> {
                    orderRepository.delete(existingOrder);
                    return true;
                })
                .orElse(false);
    }
}
