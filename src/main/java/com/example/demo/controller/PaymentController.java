package com.example.demo.controller;

import java.math.BigDecimal;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.PaymentEvent;
import com.example.demo.service.PaymentProducer;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentProducer paymentProducer;

    public PaymentController(PaymentProducer paymentProducer) {
        this.paymentProducer = paymentProducer;
    }

    @PostMapping("/{accountId}")
    public String send(@PathVariable String accountId, @RequestParam BigDecimal amount) {
        PaymentEvent event = new PaymentEvent("PAY-" + System.nanoTime(), accountId, amount, "INR");
        paymentProducer.send(event);
        return "Payment sent";
    }

}
