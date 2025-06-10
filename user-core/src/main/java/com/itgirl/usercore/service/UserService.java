package com.itgirl.usercore.service;

import com.itgirl.common.ActivationMessage;
import com.itgirl.usercore.kafka.ActivationProducer;
import com.itgirl.usercore.model.User;
import com.itgirl.usercore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final ActivationProducer activationProducer;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void registerUser(String name, String email, String password) {
        String activationKey = UUID.randomUUID().toString();

        ActivationMessage message = new ActivationMessage(
                email,
                name,
                password,
                activationKey
        );

        activationProducer.sendActivationMessage(message);
    }

    public void activateUserByKey(String activationKey) {
        Optional<User> userOpt = repository.findByActivationKey(activationKey);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setActive(true);
            user.setActivationKey(null); // clear
            repository.save(user);
        } else {
            throw new IllegalArgumentException("Invalid activation key");
        }
    }

    public void saveActivatedUser(ActivationMessage message) {
        User user = new User();
        user.setEmail(message.getEmail());
        user.setName(message.getName());
        user.setPassword(passwordEncoder.encode(message.getPassword()));
        user.setActivationKey(message.getActivationKey());
        user.setActive(false);
        user.setCreatedAt(Instant.now());
        repository.save(user);
    }
}
