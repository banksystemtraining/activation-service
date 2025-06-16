package com.itgirl.usercore.service;

import com.itgirl.common.ActivationMessage;
import com.itgirl.usercore.dto.UserCreateRequest;
import com.itgirl.usercore.exception.EmailAlreadyExistsException;
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

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final OutboxRepository outboxRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final RedisTemplate <String, UUID> redisTemplate;
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
        redisTemplate.opsForValue().set(activationKeyToString,id);
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
