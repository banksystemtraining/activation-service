package com.itgirl.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistrationRequest(
        @Size(min = 2, max = 30, message = "Name must be at least 2 and no more than 8 characters.")
        @NotBlank(message = "Enter the name.")
        String name,

        @Size(min = 2, max = 30, message = "Surname must be at least 2 and no more than 8 characters.")
        @NotBlank(message = "Enter the surname.")
        String surname,

        @Email (message = "Must be a correct email address.")
        @NotBlank(message = "Enter the email.")
        String email,

        @Size(min = 8, max = 30, message = "Password must be at least 8 and no more than 8 characters.")
        String password1,

        @Size(min = 8, max = 30, message = "Password must be at least 8 and no more than 8 characters.")
        String password2) {
}