package com.example.kafkastompmessaging.kafka;

import com.example.kafkastompmessaging.model.NotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static com.example.kafkastompmessaging.model.NotificationMessage.TOPIC;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationKafkaConsumer {

    private final SimpMessagingTemplate template;

    @KafkaListener(topics = TOPIC)
    public void handle(@Payload NotificationMessage notificationMessage, Acknowledgment acknowledgment) {
        log.info("Consuming, notificationMessage=[{}]", notificationMessage);

        //TODO need optimization
        Arrays.stream(notificationMessage.getRecipients()).forEach((recipient) -> template.convertAndSend("/topic/notification/" + recipient, notificationMessage));
        acknowledgment.acknowledge();
    }

}
