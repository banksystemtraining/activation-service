package com.itgirl.notificationservice.exception;

import com.itgirl.notificationservice.log.EmailSendException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

    @RestControllerAdvice
    public class GlobalExceptionHandler {

        @ExceptionHandler(EmailSendException.class)
        public ResponseEntity<String> handleEmailError(EmailSendException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send email to " + e.getEmail());
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<String> handleAll(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error: " + e.getMessage());
    }
}
