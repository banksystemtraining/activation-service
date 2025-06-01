package com.itgirl.usercore.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "notification-service", url = "http://${notification-service.base-url}")
public interface NotificationClient {

    @PostMapping("/activation")
    void sendActivationEmail(@RequestParam String email, @RequestParam String activationKey);
}