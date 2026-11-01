package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.kafka.autoconfigure.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;

@Configuration
public class KafkaRetryConfig {

    // Used by @RetryableTopic for the retry and DLT topics, so they run fewer consumer threads than the main listener
    @Bean(name = "retryKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<Object, Object> retryKafkaListenerContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            ConsumerFactory<Object, Object> consumerFactory,
            @Value("${kafka.retry.concurrency:2}") int concurrency) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        configurer.configure(factory, consumerFactory);
        factory.setConcurrency(concurrency);
        return factory;
    }

}
