package com.itgirl.notificationservice.service;

import com.itgirl.notificationservice.log.EmailSendException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationSender {

    private final JavaMailSender mailSender;
    private final EmailLogService emailLogService;

    public void sendActivationEmail(String email, String activationKey) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Activation Email");
        message.setText("Your activation key is: " + activationKey);

        try {
            mailSender.send(message);
            emailLogService.logEmail(email, message.getText());
        } catch (Exception e) {
            log.error("Failed to send email", e);
            throw new EmailSendException(email, e);
        }
    }
}