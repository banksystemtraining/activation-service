package com.itgirl.notificationservice.controller;

import com.itgirl.notificationservice.service.NotificationSender;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class EmailTestController {

    private final NotificationSender notificationSender;

    @PostMapping("/send-email")
    public String sendTestEmail(@RequestParam String to) {
        String activationKey = "TEST-ACTIVATION-KEY-12345";
        notificationSender.sendActivationEmail(to, activationKey);
        return "Test activation email sent to " + to;
    }
}