package com.example.kafkastompmessaging.api;

import com.example.kafkastompmessaging.model.NotificationMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.example.kafkastompmessaging.model.NotificationMessage.TOPIC;

@RestController
@RequiredArgsConstructor
public class NotificationApi {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @PostMapping("message")
    public void send(@RequestBody NotificationMessage message) {
        kafkaTemplate.send(TOPIC, message.getOrderNumber(), message);
    }

}
