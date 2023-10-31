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

    public static Payment mapToPayment(PaymentDTO paymentDTO) {
        return new Payment(paymentDTO.getPaypalPaymentId());
    }

    public static PaymentDTO mapToPaymentDTO(Payment payment) {
        return new PaymentDTO(
                payment.getPaypalPaymentId()
        );
    }
}
