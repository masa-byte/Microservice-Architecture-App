package com.walcart.ordermicroservice.controllers;

import com.walcart.ordermicroservice.domain.dtos.OrderItemDTO;
import com.walcart.ordermicroservice.domain.dtos.OrderItemDTO;
import com.walcart.ordermicroservice.domain.entities.OrderItem;
import com.walcart.ordermicroservice.domain.entities.OrderItem;
import com.walcart.ordermicroservice.services.OrderItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/orderItems")
public class OrderItemController {
    private final OrderItemService orderItemService;

    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    @PostMapping()
    public ResponseEntity<OrderItemDTO> createOrderItem(OrderItemDTO orderItemDTO) {
        return new ResponseEntity<>(
                OrderItemDTO.mapToOrderItemDTO(orderItemService.createOrderItem(orderItemDTO)),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderItemDTO> getOrderItemById(@PathVariable("id") long id) {
        return orderItemService.getOrderItemById(id)
                .map(value -> new ResponseEntity<>(OrderItemDTO.mapToOrderItemDTO(value), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping()
    public ResponseEntity<Iterable<OrderItemDTO>> getAllOrders() {
        List<OrderItem> orderItems = orderItemService.getAllOrderItems();
        List<OrderItemDTO> orderDTOs = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            orderDTOs.add(OrderItemDTO.mapToOrderItemDTO(orderItem));
        }
        return new ResponseEntity<>(orderDTOs, HttpStatus.OK);
    }

    @PutMapping("/status/{id}")
    public ResponseEntity<OrderItemDTO> updateItemRatedStatus(@PathVariable("id") long id, @RequestBody Boolean rated) {
        orderItemService.updateItemRatedStatus(id, rated);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
