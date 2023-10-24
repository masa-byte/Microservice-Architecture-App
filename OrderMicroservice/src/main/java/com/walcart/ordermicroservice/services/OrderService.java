package com.walcart.ordermicroservice.services;

import com.walcart.ordermicroservice.domain.dtos.OrderDTO;
import com.walcart.ordermicroservice.domain.dtos.PaymentDTO;
import com.walcart.ordermicroservice.domain.entities.Order;
import com.walcart.ordermicroservice.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        return orderRepository.save(new Order(
                orderDTO.getTotalPrice(),
                orderDTO.getStatus(),
                null,
                orderDTO.getShipmentAddress(),
                PaymentDTO.mapToPayment(orderDTO.getPaymentDTO()),
                orderDTO.getItems(),
                orderDTO.getCustomerId()));
    }

    public Optional<Order> getOrderById(long id) {
        return orderRepository.findById(id);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order updateOrder(long id, OrderDTO updatedOrderDTO) {
        return orderRepository.findById(id)
                .map(existingOrder -> {
                    existingOrder.setStatus(updatedOrderDTO.getStatus());
                    existingOrder.setShipped(updatedOrderDTO.getShipped());
                    return orderRepository.save(existingOrder);
                })
                .orElse(null);
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