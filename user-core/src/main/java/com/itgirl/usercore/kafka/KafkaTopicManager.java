package com.itgirl.usercore.kafka;


import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Properties;

@Component
@Slf4j
public class KafkaTopicManager {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.topic.register}")
    private String topic;

    @PostConstruct
    public void resetKafkaTopic() {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        try (AdminClient adminClient = AdminClient.create(props)) {
            adminClient.deleteTopics(List.of(topic)).all().get();
            log.info("Deleted Kafka topic '{}'", topic);

            NewTopic newTopic = new NewTopic(topic, 1, (short) 1);
            adminClient.createTopics(List.of(newTopic)).all().get();
            log.info("Created Kafka topic '{}'", topic);
        } catch (Exception e) {
            log.warn("Failed to reset Kafka topic '{}': {}", topic, e.getMessage());
        }
    }
}