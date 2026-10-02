package com.example.demo.service;

import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import com.example.demo.dto.PaymentEvent;

@Service 
public class PaymentConsumer {


    @RetryableTopic(
        attempts = "4",
        backOff = @BackOff(delay = 3000, multiplier = 1.0),
        topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE)
    @KafkaListener(topics = "payment-events", groupId = "payment-service")
    public void consume(PaymentEvent event) {
        System.out.println("Received event: " + event.transactionId());

        if("POISON".equals(event.transactionId())){
            System.out.println("Received poison pill. throwing exception..");
            throw new RuntimeException("Payment processing failed for transaction: " + event.transactionId());
        }

        System.out.println("Payment processed successfully: "+ event.transactionId());
    }

    @DltHandler
    public void handleDlt(PaymentEvent event, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        System.out.println("Received event in DLT: " + event.transactionId());
    }

}
