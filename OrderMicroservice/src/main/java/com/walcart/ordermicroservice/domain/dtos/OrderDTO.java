package com.walcart.ordermicroservice.domain.dtos;

import com.walcart.ordermicroservice.domain.entities.Address;
import com.walcart.ordermicroservice.domain.entities.Order;
import com.walcart.ordermicroservice.domain.entities.OrderItem;
import com.walcart.ordermicroservice.domain.enumerations.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long id;
    private BigDecimal totalPrice;
    private OrderStatus status;
    private ZonedDateTime shipped;
    private Address shipmentAddress;
    private PaymentDTO paymentDTO;
    private List<OrderItem> items;
    private Long customerId;

    public static Order mapToOrder(OrderDTO orderDTO) {
        return new Order(
                orderDTO.getTotalPrice(),
                orderDTO.getStatus(),
                orderDTO.getShipped(),
                orderDTO.getShipmentAddress(),
                PaymentDTO.mapToPayment(orderDTO.getPaymentDTO()),
                orderDTO.getItems(),
                orderDTO.getCustomerId());
    }

    public static OrderDTO mapToOrderDTO(Order order) {
        return new OrderDTO(
                order.getId(),
                order.getTotalPrice(),
                order.getStatus(),
                order.getShipped(),
                order.getShipmentAddress(),
                PaymentDTO.mapToPaymentDTO(order.getPayment()),
                order.getItems(),
                order.getCustomerId());
    }
}
