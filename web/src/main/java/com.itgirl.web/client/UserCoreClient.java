package com.itgirl.web.client;

import com.itgirl.web.dto.UserCreateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "user-core", url = "${feign.client.user-core.url}")
public interface UserCoreClient {
    @PostMapping(value = "/api/users")
    ResponseEntity<Void> createUser(@RequestBody UserCreateRequest userCreateRequest);
}