package com.rba.creditcardapp.service;

import com.rba.creditcardapp.api.NewCardRequestApi;
import com.rba.creditcardapp.dto.NewCardRequestDto;
import com.rba.creditcardapp.model.NewCardRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

@Service
@Slf4j
public class ExternalApiClientService {

    private final NewCardRequestApi newCardRequestApi;

    @Value("${external.api.enabled}")
    private boolean externalApiEnabled;

    public ExternalApiClientService(NewCardRequestApi newCardRequestApi) {
        this.newCardRequestApi = newCardRequestApi;
    }

    @Retryable(value = {RestClientException.class}, maxAttempts = 3)
    public boolean forwardClientToExternalApi(NewCardRequestDto cardRequest) {
        if (!externalApiEnabled) {
            log.info("External API forwarding is disabled. Simulation mode active.");
            log.info("WOULD SEND CLIENT DATA: {} {} (OIB: {})",
                    cardRequest.getFirstName(), cardRequest.getLastName(), cardRequest.getOib());
            simulateApiCall(cardRequest);
            return true;
        }

        try {
            log.info("Forwarding client data to external API: {} {}",
                    cardRequest.getFirstName(), cardRequest.getLastName());

            NewCardRequest apiRequest = convertToApiModel(cardRequest);

            ResponseEntity<Void> response = newCardRequestApi.apiV1CardRequestPost(apiRequest);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Successfully forwarded to external API for OIB: {}. Status: {}",
                        cardRequest.getOib(), response.getStatusCode());
                return true;
            } else {
                log.warn("External API returned non-success status: {} for OIB: {}",
                        response.getStatusCode(), cardRequest.getOib());
                return false;
            }
        } catch (Exception e) {
            log.error("Error calling external API for OIB: {}. Error: {}",
                    cardRequest.getOib(), e.getMessage());
            log.debug("Full error details:", e);
            throw new RuntimeException("Failed to call external API for OIB: " + cardRequest.getOib(), e);
        }
    }

    private NewCardRequest convertToApiModel(NewCardRequestDto dto) {
        NewCardRequest apiRequest = new NewCardRequest();
        apiRequest.setFirstName(dto.getFirstName());
        apiRequest.setLastName(dto.getLastName());
        apiRequest.setOib(dto.getOib());
        apiRequest.setStatus(dto.getStatus());
        return apiRequest;
    }

    private void simulateApiCall(NewCardRequestDto cardRequest) {
        try {
            Thread.sleep(100);
            log.info("SIMULATION: External API would return 201 Created for OIB: {}",
                    cardRequest.getOib());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("API simulation was interrupted");
        }
    }
}
