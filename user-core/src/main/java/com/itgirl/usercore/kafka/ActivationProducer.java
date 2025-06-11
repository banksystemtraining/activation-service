package com.itgirl.usercore.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import com.itgirl.common.ActivationMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActivationProducer {

    private final KafkaTemplate<String, ActivationMessage> kafkaTemplate;

    @Value("${spring.kafka.topic.register}")
    private String topic;

    public void sendActivationMessage(ActivationMessage message) {
        kafkaTemplate.send(topic, message.getEmail(), message);
    }
}
