package com.itgirl.usercore.service;

import com.itgirl.usercore.repository.OutboxRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxCleaner {
    private final OutboxRepository outboxRepository;

    @PostConstruct
    public void clearOutbox() {
        outboxRepository.deleteAll(); // Удаляет все записи
        log.info("Outbox table cleared at startup");
    }
}
