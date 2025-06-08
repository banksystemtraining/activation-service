package com.itgirl.web.dto;

public record UserCreateRequest(
        String name,
        String surname,
        String email,
        String password) {
}