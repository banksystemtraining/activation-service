package com.itgirl.usercore.controller;

import com.itgirl.usercore.dto.AuthenticationRequest;
import com.itgirl.usercore.dto.AuthenticationResponse;
import com.itgirl.usercore.dto.UserCreateRequest;
import com.itgirl.usercore.model.User;
import com.itgirl.usercore.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping(value = "/users", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createUser(@RequestBody UserCreateRequest userCreateRequest) {
        userService.createUser(userCreateRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/auth")
    public ResponseEntity<AuthenticationResponse> authUser(@RequestBody AuthenticationRequest authenticationRequest) {
        User user = userService.authUser(authenticationRequest);
        AuthenticationResponse authenticationResponse = new AuthenticationResponse(
                user.getId(),
                user.getEmail()
        );
        return ResponseEntity.ok(authenticationResponse);
    }
}