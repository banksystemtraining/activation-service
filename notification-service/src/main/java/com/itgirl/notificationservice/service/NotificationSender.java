package com.itgirl.notificationservice.service;

import com.itgirl.common.ActivationMessage;
import com.itgirl.notificationservice.log.EmailSendException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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
        System.out.println("📬 Sending email to: " + message.getEmail());
        String activationUrl = "http://localhost:8082/api/activate?key=" + message.getActivationKey();
        String content = "Welcome " + message.getName() + "! Click the link to activate your account:\n" + activationUrl;
        sendEmail(message.getEmail(), "Activate your account", content);
    }

    private void sendEmail(String email, String subject, String content) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setTo(email);
            helper.setSubject(subject);

            String htmlContent = content.replaceAll(
                    "(http://[^\\s]+)",
                    "<a href=\"$1\">$1</a>"
            );

            htmlContent = htmlContent.replaceAll("\n", "<br>");
            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);
            emailLogService.logEmail(email, content);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}", email, e);
            throw new EmailSendException(email, e);
        }
    }
}