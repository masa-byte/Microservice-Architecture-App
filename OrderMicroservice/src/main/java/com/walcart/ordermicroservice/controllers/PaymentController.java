package com.walcart.ordermicroservice.controllers;

import com.walcart.ordermicroservice.domain.dtos.PaymentDTO;
import com.walcart.ordermicroservice.domain.entities.Payment;
import com.walcart.ordermicroservice.services.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping()
    public ResponseEntity<PaymentDTO> createPayment(PaymentDTO paymentDTO) {
        return new ResponseEntity<>(
                PaymentDTO.mapToPaymentDTO(paymentService.createPayment(paymentDTO)),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDTO> getPaymentById(@PathVariable("id") long id) {
        return paymentService.getPaymentById(id)
                .map(value -> new ResponseEntity<>(PaymentDTO.mapToPaymentDTO(value), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/paypal/{paypalPaymentId}")
    public ResponseEntity<PaymentDTO> getPaymentByPaypalPaymentId(@PathVariable("paypalPaymentId") String paypalPaymentId) {
        return paymentService.getPaymentByPaypalPaymentId(paypalPaymentId)
                .map(value -> new ResponseEntity<>(PaymentDTO.mapToPaymentDTO(value), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping()
    public ResponseEntity<Iterable<PaymentDTO>> getAllPayments() {
        List<Payment> payments = paymentService.getAllPayments();
        List<PaymentDTO> paymentDTOs = new ArrayList<>();
        for (Payment payment : payments) {
            paymentDTOs.add(PaymentDTO.mapToPaymentDTO(payment));
        }
        return new ResponseEntity<>(paymentDTOs, HttpStatus.OK);
    }
}
