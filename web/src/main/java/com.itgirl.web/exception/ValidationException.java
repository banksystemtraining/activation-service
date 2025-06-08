package com.itgirl.web.exception;

public class ValidationException extends RuntimeException {
    public ValidationException (String message){
        super(message);
    }
}