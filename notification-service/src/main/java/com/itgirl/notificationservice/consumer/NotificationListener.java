package com.itgirl.notificationservice.consumer;

import com.itgirl.notificationservice.service.NotificationSender;
import com.itgirl.usercore.client.UserCoreClient;
import com.itgirl.usercore.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationListener {

    private final UserCoreClient userCoreClient;
    private final NotificationSender notificationSender;

    @KafkaListener(topics = "user-activation", groupId = "notification-group")
    public void listenUserActivation(String userIdStr) {
        UUID userId = UUID.fromString(userIdStr);
        UserDto user = userCoreClient.getUserById(userId);

        log.info("Sending activation email to: {}", user.getEmail());
        notificationSender.sendActivationEmail(user.getEmail(), userIdStr);
    }
}