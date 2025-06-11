package com.itgirl.notificationservice.service;

import com.itgirl.common.ActivationMessage;
import com.itgirl.notificationservice.log.EmailSendException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationSender {

    private final JavaMailSender mailSender;
    private final EmailLogService emailLogService;

    public void sendActivationEmail(String email, String activationKey) {
        String content = "Your activation key is: " + activationKey;
        sendEmail(email, "Activation Email", content);
    }

    public void sendRegistrationEmail(ActivationMessage message) {
        String activationUrl = "https://yourapp.com/activate?key=" + message.getActivationKey();
        String content = "Welcome " + message.getName() + "! Click the link to activate your account:\n" + activationUrl;
        sendEmail(message.getEmail(), "Activate your account", content);
    }

    private void sendEmail(String email, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setText(content);

        try {
            mailSender.send(message);
            emailLogService.logEmail(email, content);
        } catch (Exception e) {
            log.error("Failed to send email to {}", email, e);
            throw new EmailSendException(email, e);
        }
    }

    public void sendRegistrationEmail(String to, String s) {
        
    }
}