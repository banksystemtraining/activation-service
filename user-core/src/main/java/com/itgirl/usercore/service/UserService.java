package com.itgirl.usercore.service;

import com.itgirl.common.ActivationMessage;
import com.itgirl.usercore.dto.UserCreateRequest;
import com.itgirl.usercore.exception.*;
import com.itgirl.usercore.kafka.ActivationProducer;
import com.itgirl.usercore.model.Outbox;
import com.itgirl.usercore.model.User;
import com.itgirl.usercore.repository.OutboxRepository;
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
    private final OutboxRepository outboxRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final RedisTemplate<String, UUID> redisTemplate;
    private final ActivationProducer activationProducer;

    @Transactional
    public void createUser(UserCreateRequest userCreateRequest) {
        if (userRepository.existsByEmail(userCreateRequest.email())) {
            throw new EmailAlreadyExistsException("Email " + userCreateRequest.email() + " already exists.");
        }
        UUID id = UUID.randomUUID();
        User user = new User();
        user.setId(id);
        user.setName(userCreateRequest.name());
        user.setSurname(userCreateRequest.surname());
        user.setEmail(userCreateRequest.email());
        user.setPassword(passwordEncoder.encode(userCreateRequest.password()));

        userRepository.save(user);

        UUID activationKey = UUID.randomUUID();
        Outbox outbox = new Outbox();
        outbox.setActivationKey(activationKey);
        outbox.setEmail(userCreateRequest.email());

        outboxRepository.save(outbox);

        String activationKeyToString = activationKey.toString();
        redisTemplate.opsForValue().set(activationKeyToString, id, 1, TimeUnit.HOURS);

        //здесь нужно организовать ActivationMessage для kafka
        //  и вставить activationProducer.sendActivationMessage(message);
        // я так думаю :)
    }

    @Transactional
    public void activateUser(String activationKey) {
        //здесь будет получение activationKey из строки запроса
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
//
//    public void activateUserByKey(String activationKey) {
//        Optional<User> userOpt = repository.findByActivationKey(activationKey);
//        if (userOpt.isPresent()) {
//            User user = userOpt.get();
//            user.setActive(true);
//            user.setActivationKey(null); // clear
//            repository.save(user);
//        } else {
//            throw new IllegalArgumentException("Invalid activation key");
//        }
//    }
//
////    public void saveActivatedUser(ActivationMessage message) {
//        User user = new User();
//        user.setEmail(message.getEmail());
//        user.setName(message.getName());
//        user.setPassword(passwordEncoder.encode(message.getPassword()));
//        user.setActivationKey(message.getActivationKey());
//        user.setActive(false);
//        user.setCreated(Instant.now());
//        repository.save(user);
//    }
}
