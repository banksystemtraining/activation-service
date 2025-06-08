package com.itgirl.usercore.controller;

import com.itgirl.usercore.dto.UserCreateRequest;
import com.itgirl.usercore.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<Void> createUser (@RequestBody UserCreateRequest userCreateRequest){
        userService.register(userCreateRequest);
        return ResponseEntity.ok().build();
    }
}