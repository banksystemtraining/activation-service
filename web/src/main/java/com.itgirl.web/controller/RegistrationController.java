package com.itgirl.web.controller;

import com.itgirl.web.client.UserCoreClient;
import com.itgirl.web.dto.ApiResponse;
import com.itgirl.web.dto.RegistrationRequest;
import com.itgirl.web.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.ValidatorException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RegistrationController {
    private final RegistrationService registrationService;
    private final UserCoreClient userCoreClient;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerUser(
            @Valid @RequestBody RegistrationRequest registrationRequest, BindingResult bindingResult) throws ValidatorException {
        log.info("Received registration request for email: {}", registrationRequest.email());
        registrationService.register(registrationRequest, bindingResult);
        log.info("User with email: {} registered successfully", registrationRequest.email());
        return ResponseEntity.ok(new ApiResponse("User registered successfully", HttpStatus.OK.value(), Instant.now()));
    }

    @GetMapping("/activate")
    public ResponseEntity<?> activate(@RequestParam String key) {
        log.info("Received activation request with key: {}", key);
        userCoreClient.activateUser(key);
        log.info("Account activated successfully");
        return ResponseEntity.ok("Account activated successfully!");
    }
}