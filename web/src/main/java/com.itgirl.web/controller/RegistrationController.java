package com.itgirl.web.controller;

import com.itgirl.web.client.UserCoreClient;
import com.itgirl.web.dto.ApiResponse;
import com.itgirl.web.dto.RegistrationRequest;
import com.itgirl.web.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.ValidatorException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RegistrationController {
    private final RegistrationService registrationService;
    private final UserCoreClient userCoreClient;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerUser(
            @Valid @RequestBody RegistrationRequest registrationRequest, BindingResult bindingResult) throws ValidatorException {
        registrationService.register(registrationRequest, bindingResult);
        return ResponseEntity.ok(new ApiResponse("User registered successfully", HttpStatus.OK.value(), Instant.now()));
    }

    @GetMapping("/activate")
    public ResponseEntity<?> activate(@RequestParam String key) {
        try {
            userCoreClient.activateUser(key);
            return ResponseEntity.ok("Account activated successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Activation failed: " + e.getMessage());
        }
    }
}