package com.rba.creditcardapp.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicsConfig {

    @Value("${kafka.topics.card-status:card-status-topic}")
    private String cardStatusTopic;

    @Value("${kafka.topics.card-production:card-production-topic}")
    private String cardProductionTopic;

    @Bean
    public NewTopic cardStatusTopic() {
        return TopicBuilder.name(cardStatusTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic cardProductionTopic() {
        return TopicBuilder.name(cardProductionTopic)
                .partitions(2)
                .replicas(1)
                .build();
    }
}