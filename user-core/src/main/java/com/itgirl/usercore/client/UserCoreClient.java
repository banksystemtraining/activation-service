package com.itgirl.usercore.client;

import com.itgirl.usercore.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "web", url = "${feign.client.web.url}")
public interface UserCoreClient {

    @GetMapping("/api/users/{id}")
    UserDto getUserById(@PathVariable("id") UUID id);
}