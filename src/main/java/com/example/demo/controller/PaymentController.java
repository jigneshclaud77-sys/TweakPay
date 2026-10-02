package com.example.demo.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @PostMapping
    public String send(@RequestBody PaymentEvent event) {
        paymentProducer.send(event);
        return "Payment sent";
    }

}
