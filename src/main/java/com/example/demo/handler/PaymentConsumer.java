package com.example.demo.handler;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.SameIntervalTopicReuseStrategy;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import com.example.demo.dto.PaymentEvent;
import com.example.demo.processor.PaymentProcessorFactory;

@Service
@RetryableTopic(attempts = "4", backOff = @BackOff(delay = 3000, multiplier = 1.0),
        sameIntervalTopicReuseStrategy = SameIntervalTopicReuseStrategy.MULTIPLE_TOPICS,
        topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
        listenerContainerFactory = "retryKafkaListenerContainerFactory")
@KafkaListener(topics = "payment-events", id = "${kafka.container.id}", idIsGroup = false)
public class PaymentConsumer {

    private static final Logger log = LoggerFactory.getLogger(PaymentConsumer.class);

    @Autowired
    @Qualifier("paymentTaskExecutor")
    private AsyncTaskExecutor asyncTaskExecutor;

    @Autowired
    PaymentProcessorFactory paymentProcessor;

    @KafkaHandler
    public CompletableFuture<Void> handleEvent(@Payload PaymentEvent event, @Header(KafkaHeaders.RECEIVED_PARTITION) String partition,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic, @Header(KafkaHeaders.OFFSET) String offset) {

        log.debug("Received event: {} from partition: {} on topic: {} at offset: {}", event.transactionId(), partition, topic, offset);

        return asyncTaskExecutor.submitCompletable(paymentProcessor.create(event))
            .thenRun(() -> log.debug("Payment processing completed for transaction: {}", event.transactionId()));
    }

    @DltHandler
    public void handleDlt(PaymentEvent event, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic, @Header(KafkaHeaders.RECEIVED_PARTITION) String partition, @Header(KafkaHeaders.OFFSET) String offset) {
        log.error("Received event in DLT: {} from partition: {} on topic: {} at offset: {}", event.transactionId(), partition, topic, offset);
    }

}
