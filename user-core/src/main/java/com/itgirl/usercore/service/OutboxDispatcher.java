package com.itgirl.usercore.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itgirl.common.ActivationMessage;
import com.itgirl.usercore.model.Outbox;
import com.itgirl.usercore.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxDispatcher {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, ActivationMessage> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 5000)
    public void dispatchMessages() {
        var messages = outboxRepository.findAllByProcessedFalse();
        for (Outbox outbox : messages) {
            try {
                ActivationMessage message = objectMapper.readValue(outbox.getPayload(), ActivationMessage.class);

                kafkaTemplate.send(outbox.getTopic(), message.getEmail(), message)
                        .whenComplete((result, ex) -> {
                            if (ex == null) {
                                log.info("Sent message from outbox to Kafka: {}", message.getEmail());
                                outbox.setProcessed(true);
                                outboxRepository.save(outbox);
                            } else {
                                log.error("Failed to send message from outbox", ex);
                            }
                        });
            } catch (Exception e) {
                log.error("Error processing outbox message", e);
            }
        }
    }
}