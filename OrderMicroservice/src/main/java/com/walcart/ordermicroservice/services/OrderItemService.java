package com.walcart.ordermicroservice.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.DeliverCallback;
import com.walcart.ordermicroservice.domain.dtos.OrderItemDTO;
import com.walcart.ordermicroservice.domain.entities.OrderItem;
import com.walcart.ordermicroservice.repositories.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

@Service
public class OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private Connection connection = null;
    private Channel orderItemChannel = null;
    private final static String ORDER_ITEM_QUEUE_NAME = "order-items";
    private final String consumerName;
    private final ObjectMapper objectMapper;

    @Autowired
    public OrderItemService(OrderItemRepository orderItemRepository) throws IOException, TimeoutException {
        this.consumerName = "order-item-microservice"; // + id for replication of microservice
        this.orderItemRepository = orderItemRepository;
        this.objectMapper = new ObjectMapper();
        this.configureConnection();
        this.updateItemRatedStatusMessageBroker();
    }

    private void configureConnection() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");

        this.connection = factory.newConnection();
        this.orderItemChannel = connection.createChannel();
        this.orderItemChannel.queueDeclare(ORDER_ITEM_QUEUE_NAME, true, false, false, null);
    }

    public OrderItem createOrderItem(OrderItemDTO orderItemDTO) {
        return orderItemRepository.save(new OrderItem(
                orderItemDTO.getQuantity(),
                orderItemDTO.getProductId(),
                false)
        );
    }

    public Optional<OrderItem> getOrderItemById(long id) {
        return orderItemRepository.findById(id);
    }

    public List<OrderItem> getAllOrderItems() {
        return orderItemRepository.findAll();
    }

    public void updateItemRatedStatus(long id, boolean rated) {
        orderItemRepository.findById(id)
                .map(existingOrderItem -> {
                    existingOrderItem.setRated(rated);
                    return orderItemRepository.save(existingOrderItem);
                });
    }

    private void updateItemRatedStatusMessageBroker() throws IOException {
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            JsonNode jsonNode = objectMapper.readTree(message);
            long id = jsonNode.get("_id").asLong();
            boolean rated = jsonNode.get("_status").asBoolean();
            this.updateItemRatedStatus(id, rated);
        };
        orderItemChannel.basicConsume(ORDER_ITEM_QUEUE_NAME, true, consumerName, deliverCallback, consumerTag -> {});
    }

    // update not needed since order items can't be updated
    // delete not needed since we can only delete order items by deleting the order
}
