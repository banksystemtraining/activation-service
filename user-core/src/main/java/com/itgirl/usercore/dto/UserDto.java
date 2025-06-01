package com.itgirl.usercore.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class UserDto {
    private UUID id;
    private String name;
    private String surname;
    private String email;
    private String status;
}