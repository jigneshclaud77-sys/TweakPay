package com.example.demo.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.demo.dto.PaymentEvent;

@Service 
public class PaymentProducer {

    private static final String TOPIC = "payment-events";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PaymentProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(PaymentEvent event) {
        kafkaTemplate.send(TOPIC, event.accountId(), event);
    }
}
