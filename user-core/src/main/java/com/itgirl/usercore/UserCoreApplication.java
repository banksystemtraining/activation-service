package com.itgirl.usercore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class UserCoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserCoreApplication.class, args);
    }
}