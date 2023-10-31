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
    private Boolean rated;

    public static OrderItem mapToOrderItem(OrderItemDTO orderItemDTO) {
        return new OrderItem(
                orderItemDTO.getQuantity(),
                orderItemDTO.getProductId(),
                orderItemDTO.getRated()
        );
    }
    public static OrderItemDTO mapToOrderItemDTO(OrderItem orderItem) {
        return new OrderItemDTO(
                orderItem.getProductId(),
                orderItem.getQuantity(),
                orderItem.getRated());
    }
}
