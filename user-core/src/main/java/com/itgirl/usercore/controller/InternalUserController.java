package com.itgirl.usercore.controller;

import com.itgirl.usercore.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor


public class InternalUserController {

    private final UserService userService;

    @GetMapping("/activate")
    public void activate(@RequestParam String key) {
        userService.activateUser(key);
    }
}