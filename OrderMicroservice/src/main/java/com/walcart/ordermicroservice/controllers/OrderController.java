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
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping()
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO orderDTO) {
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

    @GetMapping("/customer/{customerId}/product/{productId}")
    public ResponseEntity<OrderDTO> getOrderByCustomerIdAndProductId(@PathVariable("customerId") long customerId, @PathVariable("productId") long productId) {
        return orderService.getOrderByCustomerIdAndProductId(customerId, productId)
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

    @PutMapping("/{id}")
    public ResponseEntity<OrderDTO> updateOrder(@PathVariable("id") long id, @RequestBody OrderDTO updatedOrderDTO) {
        Order updatedOrder = orderService.updateOrder(id, updatedOrderDTO);
        if (updatedOrder != null) {
            updatedOrderDTO = OrderDTO.mapToOrderDTO(updatedOrder);
            return new ResponseEntity<>(updatedOrderDTO, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteOrder(@PathVariable("id") long id) {
        return orderService.deleteOrder(id)
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
