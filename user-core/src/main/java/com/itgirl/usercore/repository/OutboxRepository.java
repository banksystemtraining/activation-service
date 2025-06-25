package com.itgirl.usercore.repository;

import com.itgirl.usercore.model.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends JpaRepository<Outbox, UUID> {
    List<Outbox> findAllByProcessedFalse();
}
