package com.itgirl.web.controller;

import com.itgirl.web.dto.AuthTokenResponse;
import com.itgirl.web.dto.AuthenticationRequest;
import com.itgirl.web.dto.LogoutRequest;
import com.itgirl.web.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/authentication")
    public ResponseEntity<AuthTokenResponse> authenticationUser(@Valid @RequestBody AuthenticationRequest authenticationRequest) {
        log.info("Received authentication request for user: {}", authenticationRequest.getEmail());
        String token = authenticationService.authentication(authenticationRequest);
        log.info("Generated JWT for user: {}", authenticationRequest.getEmail());
        return ResponseEntity.ok(new AuthTokenResponse(token));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody(required = false) LogoutRequest request) {
        String token = extractToken(authHeader);
        log.info("Received logout request");
        if (request != null && request.getToken() != null) {
            token = request.getToken();
            log.info("Overriding token with one form request body");
        }
        authenticationService.addToBlackList(token);
        log.info("Token added to blacklist");
        return ResponseEntity.ok().build();
    }

    private String extractToken(String authHeader) {
        log.info("Extracting token from Authorization header");
        return authHeader.replace("Bearer ", "").trim();
    }
}