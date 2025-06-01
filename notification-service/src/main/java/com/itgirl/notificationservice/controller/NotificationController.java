package com.itgirl.notificationservice.controller;

import com.itgirl.notificationservice.service.NotificationSender;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationSender notificationSender;

    @PostMapping("/activation")
    public ResponseEntity<Void> sendActivationEmail(
            @RequestParam String email,
            @RequestParam String activationKey) {

        notificationSender.sendActivationEmail(email, activationKey);
        return ResponseEntity.ok().build();
    }
}