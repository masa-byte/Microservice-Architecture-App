package com.walcart.bff.domain.dtos;

import com.walcart.bff.domain.enumerations.OrderStatus;
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
    private AddressDTO shipmentAddress;
    private PaymentDTO paymentDTO;
    private List<OrderItemDTO> items;
    private Long customerId;
}
