package com.itgirl.usercore.service;

import com.itgirl.common.ActivationMessage;
import com.itgirl.usercore.dto.UserCreateRequest;
import com.itgirl.usercore.exception.*;
import com.itgirl.usercore.kafka.ActivationProducer;
import com.itgirl.usercore.model.User;
import com.itgirl.usercore.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final RedisTemplate<String, UUID> redisTemplate;
    private final ActivationProducer activationProducer;

    @Transactional
    public void createUser(UserCreateRequest userCreateRequest) {
        if (userRepository.existsByEmail(userCreateRequest.email())) {
            throw new EmailAlreadyExistsException("Email " + userCreateRequest.email() + " already exists.");
        }
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setName(userCreateRequest.name());
        user.setSurname(userCreateRequest.surname());
        user.setEmail(userCreateRequest.email());
        user.setPassword(passwordEncoder.encode(userCreateRequest.password()));
        user.setActive(false);
        userRepository.save(user);

        UUID activationKey = UUID.randomUUID();

        redisTemplate.opsForValue().set(activationKey.toString(), userId, 1, TimeUnit.HOURS);

        ActivationMessage activationMessage = new ActivationMessage(
                user.getEmail(),
                user.getName(),
                activationKey.toString()
        );

        activationProducer.saveToOutbox(activationMessage);
    }

    @Transactional
    public void activateUser(String activationKey) {
        if (Boolean.FALSE.equals(redisTemplate.hasKey(activationKey))) {
            throw new ActivationKeyNotFoundException("Activation key not found: timeout.");
        }

        UUID id = redisTemplate.opsForValue().get(activationKey);

        if (id == null) {
            throw new InvalidActivationDataException("Invalid activation data detected.");
        }

        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found."));

        if (user.isActive()) {
            throw new UserAlreadyActivatedException("User already activated.");
        }

        user.setActive(true);
        userRepository.save(user);

        try {
            redisTemplate.delete(activationKey);
        } catch (Exception e) {
            throw new RedisOperationException("Failed to complete activation process", e);
        }
    }
}