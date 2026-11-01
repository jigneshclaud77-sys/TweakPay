package com.example.demo.processor;

import org.springframework.stereotype.Service;

import com.example.demo.dto.PaymentEvent;
import com.example.demo.service.PaymentService;

@Service
public class PaymentProcessorFactory {

    private final PaymentService paymentService;

    public PaymentProcessorFactory(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public Runnable create(PaymentEvent event) {
        return () -> paymentService.processPayment(event);
        // return new Runnable() {
        //     @Override
        //     public void run() {
        //         paymentService.processPayment(event);
        //     }
        // };
    }

}
