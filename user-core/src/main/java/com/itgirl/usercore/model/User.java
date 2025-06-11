package com.itgirl.usercore.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue
    private UUID id;

    private String email;
    private String name;
    private String password;
    private boolean active;
    private String activationKey;
    private Instant createdAt;
}
