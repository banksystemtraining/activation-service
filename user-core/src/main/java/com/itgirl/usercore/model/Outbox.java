package com.itgirl.usercore.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "outbox")
public class Outbox {
    @Id
    @Column(name = "activation_key", nullable = false, columnDefinition = "UUID")
    private UUID activationKey;

    @Column(name = "email", nullable = false, unique = true)
    private String email;
}
