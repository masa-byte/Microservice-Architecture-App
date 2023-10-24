package com.walcart.ordermicroservice.domain.dtos;

import com.walcart.ordermicroservice.domain.entities.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    private Long productId;
    private Integer quantity;
    private OrderDTO orderDTO;

    public static OrderItemDTO mapToOrderItemDTO(OrderItem orderItem) {
        return new OrderItemDTO(
                orderItem.getProductId(),
                orderItem.getQuantity(),
                OrderDTO.mapToOrderDTO(orderItem.getOrder()));
    }
}
