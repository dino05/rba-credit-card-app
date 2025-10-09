package com.rba.creditcardapp.kafka;

import com.rba.creditcardapp.dto.CardStatusUpdateDto;
import com.rba.creditcardapp.model.CardStatus;
import com.rba.creditcardapp.service.ClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Slf4j
@ConditionalOnProperty(name = "kafka.enabled", havingValue = "true", matchIfMissing = false)
public class CardStatusConsumer {

    private final ClientService clientService;

    public CardStatusConsumer(ClientService clientService) {
        this.clientService = clientService;
    }

    @KafkaListener(
            topics = "${kafka.topics.card-status:card-status-topic}",
            groupId = "${spring.kafka.consumer.group-id:card-status-group}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void receiveCardStatusUpdate(CardStatusUpdateDto statusUpdate) {

        if (statusUpdate == null) {
            log.warn("Received null payload from Kafka");
            return;
        }

        try {
            log.info("Received card status update for OIB: {}, Status: {}, Reason: {}",
                    statusUpdate.getOib(), statusUpdate.getStatus(), statusUpdate.getUpdateReason());

            if (!isValidStatusUpdate(statusUpdate)) {
                log.warn("Invalid card status update received. Skipping processing.");
                return;
            }

            clientService.updateClientStatus(statusUpdate.getOib(), statusUpdate.getStatus());

            log.info("Successfully updated card status for OIB: {} to {}",
                    statusUpdate.getOib(), statusUpdate.getStatus());

        } catch (Exception e) {
            log.error("Error processing card status update for OIB: {}",
                    statusUpdate.getOib(), e);
        }
    }

    private boolean isValidStatusUpdate(CardStatusUpdateDto statusUpdate) {
        if (statusUpdate == null) {
            log.warn("Received null status update");
            return false;
        }

        if (statusUpdate.getOib() == null || statusUpdate.getOib().length() != 11) {
            log.warn("Invalid OIB format in status update: {}", statusUpdate.getOib());
            return false;
        }

        if (statusUpdate.getStatus() == null || statusUpdate.getStatus().trim().isEmpty()) {
            log.warn("Empty status received for OIB: {}", statusUpdate.getOib());
            return false;
        }

        if (!CardStatus.isValid(statusUpdate.getStatus())) {
            log.warn("Invalid status value received: {} for OIB: {}. Valid statuses: {}",
                    statusUpdate.getStatus(), statusUpdate.getOib(), Arrays.toString(CardStatus.values()));
            return false;
        }

        return true;
    }
}