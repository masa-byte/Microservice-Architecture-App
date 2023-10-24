package com.walcart.ordermicroservice.services;

import com.walcart.ordermicroservice.domain.dtos.OrderDTO;
import com.walcart.ordermicroservice.domain.dtos.PaymentDTO;
import com.walcart.ordermicroservice.domain.entities.Payment;
import com.walcart.ordermicroservice.repositories.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment createPayment(PaymentDTO paymentDTO) {
        return paymentRepository.save(new Payment(
                paymentDTO.getPaypalPaymentId(),
                OrderDTO.mapToOrder(paymentDTO.getOrderDTO())));
    }

    public Optional<Payment> getPaymentById(long id) {
        return paymentRepository.findById(id);
    }

    public Optional<Payment> getPaymentByPaypalPaymentId(String paypalPaymentId) {
        return paymentRepository.findByPaypalPaymentId(paypalPaymentId);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    // update not needed since payments can't be updated
    // delete not needed since we can only delete payments by deleting the order
}
