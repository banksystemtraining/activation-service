package com.itgirl.usercore.service;

import com.itgirl.usercore.dto.UserDto;
import com.itgirl.usercore.model.User;
import com.itgirl.usercore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void register(UserDto userDto) {
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        user.setName(userDto.getName());
        user.setSurname(userDto.getSurname());
        user.setEmail(userDto.getEmail());
        user.setEnabled(false);

        userRepository.save(user);

        String activationKey = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(activationKey, userId.toString(), Duration.ofHours(1));

        kafkaTemplate.send("user-activation", userId.toString());
    }

    public boolean activate(String key) {
        String userIdStr = redisTemplate.opsForValue().get(key);
        if (userIdStr == null) return false;

        UUID userId = UUID.fromString(userIdStr);
        return userRepository.findById(userId).map(user -> {
            user.setEnabled(true);
            userRepository.save(user);
            redisTemplate.delete(key);
            return true;
        }).orElse(false);
    }
}