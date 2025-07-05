package com.itgirl.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthenticationRequest {
    @Email(message = "Must be a correct email address.")
    @NotBlank(message = "Enter the email.")
    private String email;

    @Size(min = 8, max = 30, message = "Password must be at least 8 and no more than 30 characters.")
    private String password;
}