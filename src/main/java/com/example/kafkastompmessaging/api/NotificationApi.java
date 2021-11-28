package com.example.kafkastompmessaging.api;

import com.example.kafkastompmessaging.model.NotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.example.kafkastompmessaging.model.NotificationMessage.TOPIC;

@Slf4j
@RestController
@RequiredArgsConstructor
public class NotificationApi {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @PostMapping("message")
    public void send(@RequestBody NotificationMessage message) {
        ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(TOPIC, message.getOrderNumber(), message);
        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onSuccess(SendResult<String, Object> result) {
                log.info("sent message='{}' with offset={}", message, result.getRecordMetadata().offset());
            }
            @Override
            public void onFailure(Throwable ex) {
                log.error("unable to send message='{}'", message, ex);
            }
        });

    }

}
