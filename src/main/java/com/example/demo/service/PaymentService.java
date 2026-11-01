package com.example.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.example.demo.dto.PaymentEvent;


@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    
    public void processPayment(PaymentEvent event) {
        if("POISON".equals(event.transactionId())){
            log.error("Received poison pill. throwing exception..");
            throw new RuntimeException("Payment processing failed for transaction: " + event.transactionId());
        }

        log.debug("Payment processed successfully: {}", event.transactionId());
    }

}
