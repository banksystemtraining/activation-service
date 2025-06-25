package com.itgirl.usercore.controller;

import com.itgirl.usercore.dto.UserCreateRequest;
import com.itgirl.usercore.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createUser(@RequestBody UserCreateRequest userCreateRequest) {
        userService.createUser(userCreateRequest);
        return ResponseEntity.ok().build();
    }
}
