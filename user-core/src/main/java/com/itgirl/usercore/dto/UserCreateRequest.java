package com.itgirl.usercore.dto;

public record UserCreateRequest(
        String name,
        String surname,
        String email,
        String password) {
}
