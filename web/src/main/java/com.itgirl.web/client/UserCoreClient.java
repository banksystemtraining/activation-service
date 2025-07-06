package com.itgirl.web.client;

import com.itgirl.web.dto.AuthenticationRequest;
import com.itgirl.web.dto.AuthenticationResponse;
import com.itgirl.web.dto.UserCreateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name = "user-core", url = "${feign.client.user-core.url}")
public interface UserCoreClient {
    @PostMapping("/api/users")
    ResponseEntity<Void> createUser(@RequestBody UserCreateRequest userCreateRequest);

    @GetMapping("/internal/activate")
    void activateUser(@RequestParam("key") String key);

    @PostMapping("/api/auth")
    AuthenticationResponse authUser(@RequestBody AuthenticationRequest authenticationRequest);
}