package com.itgirl.web.controller;

import com.itgirl.usercore.client.UserCoreClient;
import com.itgirl.usercore.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserCoreClient userCoreClient;

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable UUID id) {
        return userCoreClient.getUserById(id);
    }
}