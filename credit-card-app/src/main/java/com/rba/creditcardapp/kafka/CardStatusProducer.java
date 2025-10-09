package com.rba.creditcardapp.kafka;

import com.rba.creditcardapp.dto.CardStatusUpdateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@ConditionalOnProperty(name = "kafka.enabled", havingValue = "true", matchIfMissing = false)
public class CardStatusProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public CardStatusProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendCardStatusUpdate(String topic, CardStatusUpdateDto statusUpdate) {
        try {
            Message<CardStatusUpdateDto> message = MessageBuilder
                    .withPayload(statusUpdate)
                    .setHeader(KafkaHeaders.TOPIC, topic)
                    .setHeader(KafkaHeaders.KEY, statusUpdate.getOib())
                    .build();

            kafkaTemplate.send(message);

            log.info("Sent card status update to topic: {}, OIB: {}, Status: {}",
                    topic, statusUpdate.getOib(), statusUpdate.getStatus());

        } catch (Exception e) {
            log.error("Error sending card status update to Kafka", e);
            throw new RuntimeException("Failed to send message to Kafka", e);
        }
    }

    public void sendCardStatusUpdate(CardStatusUpdateDto statusUpdate) {
        sendCardStatusUpdate("card-status-topic", statusUpdate);
    }
}