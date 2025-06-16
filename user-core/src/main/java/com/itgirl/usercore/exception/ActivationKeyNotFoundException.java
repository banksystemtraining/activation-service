package com.itgirl.usercore.exception;

public class ActivationKeyNotFoundException extends RuntimeException{
    public ActivationKeyNotFoundException(String message){
        super(message);
    }
}
