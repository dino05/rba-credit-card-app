package com.rba.creditcardapp.kafka;

import com.rba.creditcardapp.dto.CardStatusUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CardStatusProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public CardStatusProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendCardStatusUpdate(String topic, CardStatusUpdate statusUpdate) {
        try {
            Message<CardStatusUpdate> message = MessageBuilder
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

    public void sendCardStatusUpdate(CardStatusUpdate statusUpdate) {
        sendCardStatusUpdate("card-status-topic", statusUpdate);
    }
}