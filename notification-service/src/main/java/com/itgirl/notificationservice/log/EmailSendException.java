package com.itgirl.notificationservice.log;

import lombok.Getter;

@Getter
public class EmailSendException extends RuntimeException {
    private final String email;

    public EmailSendException(String email, Throwable cause) {
        super("Failed to send email to " + email, cause);
        this.email = email;
    }
}
