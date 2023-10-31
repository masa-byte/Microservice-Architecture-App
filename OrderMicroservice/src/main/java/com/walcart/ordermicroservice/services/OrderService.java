package com.walcart.ordermicroservice.services;

import com.walcart.ordermicroservice.domain.dtos.*;
import com.walcart.ordermicroservice.domain.entities.Order;
import com.walcart.ordermicroservice.domain.entities.OrderItem;
import com.walcart.ordermicroservice.domain.entities.Payment;
import com.walcart.ordermicroservice.domain.enumerations.OrderStatus;
import com.walcart.ordermicroservice.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
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
