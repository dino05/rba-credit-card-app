package com.rba.creditcardapp.service;

import com.rba.creditcardapp.model.NewCardRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class ExternalApiClientService {

    private final RestTemplate restTemplate;

    @Value("${external.api.url}")
    private String externalApiBaseUrl;

    @Value("${external.api.card-request-endpoint}")
    private String cardRequestEndpoint;

    @Value("${external.api.enabled}")
    private boolean externalApiEnabled;

    public ExternalApiClientService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void forwardCardRequestToExternalApi(NewCardRequest cardRequest) {
        if (!externalApiEnabled) {
            log.info("External API forwarding is disabled. Simulation mode active.");
            log.info("WOULD SEND TO: {}{}", externalApiBaseUrl, cardRequestEndpoint);
            log.info("DATA: First Name: {}, Last Name: {}, OIB: {}, Status: {}",
                    cardRequest.getFirstName(), cardRequest.getLastName(),
                    cardRequest.getOib(), cardRequest.getStatus());
            simulateApiCall(cardRequest);
            return;
        }

        try {
            String externalApiUrl = externalApiBaseUrl + cardRequestEndpoint;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<NewCardRequest> request = new HttpEntity<>(cardRequest, headers);

            log.info("Sending card request to external API: {}", externalApiUrl);
            log.debug("Request payload: {}", cardRequest);

            ResponseEntity<String> response = restTemplate.exchange(
                    externalApiUrl, HttpMethod.POST, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Successfully forwarded to external API for OIB: {}. Status: {}",
                        cardRequest.getOib(), response.getStatusCode());
            } else {
                log.warn("External API returned non-success status: {} for OIB: {}",
                        response.getStatusCode(), cardRequest.getOib());
            }
        } catch (Exception e) {
            log.error("Error calling external API for OIB: {}. Error: {}",
                    cardRequest.getOib(), e.getMessage());
            log.debug("Full error details:", e);
        }
    }

    private void simulateApiCall(NewCardRequest cardRequest) {
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
