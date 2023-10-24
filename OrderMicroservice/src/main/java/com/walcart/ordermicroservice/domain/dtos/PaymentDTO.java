package com.walcart.ordermicroservice.domain.dtos;

import com.walcart.ordermicroservice.domain.entities.Order;
import com.walcart.ordermicroservice.domain.entities.Payment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private String paypalPaymentId;
    private OrderDTO orderDTO;

    public static Payment mapToPayment(PaymentDTO paymentDTO) {
        return new Payment(
                paymentDTO.getPaypalPaymentId(),
                OrderDTO.mapToOrder(paymentDTO.getOrderDTO())
        );
    }

    public static PaymentDTO mapToPaymentDTO(Payment payment) {
        return new PaymentDTO(
                payment.getPaypalPaymentId(),
                OrderDTO.mapToOrderDTO(payment.getOrder())
        );
    }
}
