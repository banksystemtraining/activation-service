package com.itgirl.usercore.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itgirl.usercore.model.Outbox;
import com.itgirl.usercore.repository.OutboxRepository;
import lombok.extern.slf4j.Slf4j;
import com.itgirl.common.ActivationMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivationProducer {

    private final ObjectMapper objectMapper;
    private final OutboxRepository outboxRepository;

    @Value("${spring.kafka.topic.register}")
    private String topic;

    public void saveToOutbox(ActivationMessage message) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(message);
            Outbox outbox = Outbox.builder()
                    .topic(topic)
                    .payload(jsonPayload)
                    .processed(false)
                    .activationKey(message.getActivationKey())
                    .build();
            outboxRepository.save(outbox);
            log.info("Activation message saved to outbox for {}", message.getEmail());
        } catch (Exception e) {
            log.error("Failed to serialize activation message", e);
            throw new RuntimeException("Serialization error", e);
        }
    }
}