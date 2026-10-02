package com.example.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.demo.dto.PaymentEvent;

@Service
public class PaymentProducer {

    private static final String TOPIC = "payment-events";
    private static final Logger log = LoggerFactory.getLogger(PaymentProducer.class);

    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    public PaymentProducer(KafkaTemplate<String, PaymentEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(PaymentEvent event) {
        // keyed by transactionId: same transaction always lands on the same partition, in order
        kafkaTemplate.send(TOPIC, event.transactionId(), event).whenComplete((r, ex) -> {
            if (ex != null)
                log.error("Send failed", ex);
            else
                log.info("partition={} offset={}", r.getRecordMetadata().partition(), r.getRecordMetadata().offset());
        });
    }
}
