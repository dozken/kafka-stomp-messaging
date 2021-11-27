package com.example.kafkastompmessaging.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.TimeoutException;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.adapter.RetryingMessageListenerAdapter;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class ConsumerConfig {


    @Bean
    public ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            ConsumerFactory<Object, Object> kafkaConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        configurer.configure(factory, kafkaConsumerFactory);
        factory.setConcurrency(1);
        factory.setRecoveryCallback((recoveryCallback -> {
            log.info("RecoveryCallback, recoveryCallback=[{}], error=[{}]", recoveryCallback, recoveryCallback.getLastThrowable().getClass().getCanonicalName());
//                Object record = recoveryCallback.getAttribute(RetryingMessageListenerAdapter.CONTEXT_RECORD);
            Object acknowledgment = recoveryCallback.getAttribute(RetryingMessageListenerAdapter.CONTEXT_ACKNOWLEDGMENT);
            //TODO refactor
            ((Acknowledgment) acknowledgment).acknowledge();

            return null;
        }));

        factory.setErrorHandler(((exception, data) -> log.error("Error in process with Exception {} and the record is {}", exception, data)));

        factory.setRetryTemplate(retryTemplate());
        return factory;
    }


    private RetryTemplate retryTemplate() {

        RetryTemplate retryTemplate = new RetryTemplate();

        /* here retry policy is used to set the number of attempts to retry and what exceptions you wanted to try and what you don't want to retry.*/

        retryTemplate.setRetryPolicy(getSimpleRetryPolicy());
//		retryTemplate.setBackOffPolicy(getBackOffPolicy());
        return retryTemplate;
    }

    private BackOffPolicy getBackOffPolicy() {
        ExponentialBackOffPolicy exponentialBackOffPolicy = new ExponentialBackOffPolicy();
        exponentialBackOffPolicy.setInitialInterval(ExponentialBackOffPolicy.DEFAULT_INITIAL_INTERVAL);
        exponentialBackOffPolicy.setMaxInterval(Duration.ofMinutes(5).toMillis());
        exponentialBackOffPolicy.setMultiplier(5);

        return exponentialBackOffPolicy;
    }

    private SimpleRetryPolicy getSimpleRetryPolicy() {
        Map<Class<? extends Throwable>, Boolean> exceptionMap = new HashMap<>();
        exceptionMap.put(IllegalStateException.class, true);
        exceptionMap.put(TimeoutException.class, true);

        return new SimpleRetryPolicy(3, exceptionMap, true);
    }

}
