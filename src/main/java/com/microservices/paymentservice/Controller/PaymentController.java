package com.microservices.paymentservice.Controller;

import com.microservices.paymentservice.Service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<String> pay(
            @RequestParam(required = false) String failure,
            @RequestParam(required = false, defaultValue = "100") int duration
    ) {
        return ResponseEntity.ok(paymentService.processPayment(failure, duration));
    }
}