package com.itgirl.notificationservice.kafka;

import com.itgirl.common.ActivationMessage;
import com.itgirl.notificationservice.service.NotificationSender;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegistrationListener {

    private final NotificationSender notificationSender;

    @KafkaListener(topics = "${spring.kafka.topic.register}", groupId = "notification-group", containerFactory = "kafkaListenerContainerFactory")
    public void handleUserRegistration(ActivationMessage message) {
        notificationSender.sendRegistrationEmail(message);
    }
}


