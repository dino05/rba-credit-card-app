package com.rba.creditcardapp.controller;

import com.rba.creditcardapp.dto.CardStatusUpdateDto;
import com.rba.creditcardapp.kafka.CardStatusProducer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/kafka-test")
@Tag(name = "Kafka Test", description = "API for testing Kafka functionality")
@AllArgsConstructor
@ConditionalOnProperty(name = "kafka.enabled", havingValue = "true", matchIfMissing = false)
public class KafkaTestController {

    private final CardStatusProducer cardStatusProducer;

    @PostMapping("/card-status")
    @Operation(summary = "Send a test card status update to Kafka")
    public ResponseEntity<String> sendTestCardStatus(@RequestBody CardStatusUpdateDto statusUpdate) {
        try {
            cardStatusProducer.sendCardStatusUpdate(statusUpdate);
            return ResponseEntity.ok("Card status update sent to Kafka successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error sending message to Kafka: " + e.getMessage());
        }
    }

    @PostMapping("/card-status/{oib}/{status}")
    @Operation(summary = "Send a simple card status update")
    public ResponseEntity<String> sendSimpleCardStatus(
            @PathVariable String oib,
            @PathVariable String status,
            @RequestParam(defaultValue = "Status updated via API") String reason) {

        CardStatusUpdateDto statusUpdate = new CardStatusUpdateDto(oib, status, reason);

        try {
            cardStatusProducer.sendCardStatusUpdate(statusUpdate);
            return ResponseEntity.ok(String.format(
                    "Status update sent for OIB: %s, Status: %s", oib, status));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}