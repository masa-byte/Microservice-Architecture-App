package com.walcart.ordermicroservice.services;

import com.walcart.ordermicroservice.domain.dtos.OrderDTO;
import com.walcart.ordermicroservice.domain.dtos.OrderItemDTO;
import com.walcart.ordermicroservice.domain.entities.OrderItem;
import com.walcart.ordermicroservice.repositories.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderItemService {
    private final OrderItemRepository orderItemRepository;

    @Autowired
    public OrderItemService(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    public OrderItem createOrderItem(OrderItemDTO orderItemDTO) {
        return orderItemRepository.save(new OrderItem(
                orderItemDTO.getQuantity(),
                orderItemDTO.getProductId(),
                OrderDTO.mapToOrder(orderItemDTO.getOrderDTO()))
        );
    }

    public Optional<OrderItem> getOrderItemById(long id) {
        return orderItemRepository.findById(id);
    }

    public List<OrderItem> getAllOrderItems() {
        return orderItemRepository.findAll();
    }

    // update not needed since order items can't be updated
    // delete not needed since we can only delete order items by deleting the order
}
