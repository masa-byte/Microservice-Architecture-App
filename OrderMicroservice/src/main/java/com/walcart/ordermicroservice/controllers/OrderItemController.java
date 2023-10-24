package com.walcart.ordermicroservice.controllers;

import com.walcart.ordermicroservice.domain.dtos.OrderDTO;
import com.walcart.ordermicroservice.domain.entities.Order;
import com.walcart.ordermicroservice.services.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/orderItems")
public class OrderItemController {
    private final OrderService orderService;

    public OrderItemController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping()
    public ResponseEntity<OrderDTO> createOrder(OrderDTO orderDTO) {
        return new ResponseEntity<>(
                OrderDTO.mapToOrderDTO(orderService.createOrder(orderDTO)),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable("id") long id) {
        return orderService.getOrderById(id)
                .map(value -> new ResponseEntity<>(OrderDTO.mapToOrderDTO(value), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping()
    public ResponseEntity<Iterable<OrderDTO>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        List<OrderDTO> orderDTOs = new ArrayList<>();
        for (Order order : orders) {
            orderDTOs.add(OrderDTO.mapToOrderDTO(order));
        }
        return new ResponseEntity<>(orderDTOs, HttpStatus.OK);
    }
}
