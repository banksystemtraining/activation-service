package com.itgirl.web.controller;

import com.itgirl.web.dto.ApiResponse;
import com.itgirl.web.dto.RegistrationRequest;
import com.itgirl.web.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.ValidatorException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RegistrationController {
    private final RegistrationService registrationService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerUser(
            @Valid @RequestBody RegistrationRequest registrationRequest, BindingResult bindingResult) throws ValidatorException {
        registrationService.register(registrationRequest, bindingResult);
        return ResponseEntity.ok(new ApiResponse("User registered successfully", HttpStatus.OK.value(), Instant.now()));
    }
}