package com.walcart.bff.domain.wrapper;

import com.walcart.bff.domain.dtos.OrderDTO;
import com.walcart.bff.domain.dtos.OrderItemDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    private OrderDTO orderDTO;
    private List<OrderItemDTO> orderItemsDTO;
}
