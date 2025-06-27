package com.itgirl.usercore.controller;

import com.itgirl.usercore.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/activate")
@RequiredArgsConstructor

public class InternalUserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<Void> activate(@RequestParam String key) {
        userService.activateUser(key);
        return ResponseEntity.ok().build();
    }
}