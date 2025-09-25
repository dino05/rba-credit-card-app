package com.rba.creditcardapp.service;

import com.rba.creditcardapp.model.NewCardRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class ExternalApiClientServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private ExternalApiClientService externalApiClientService;

    @BeforeEach
    void setUp() {
        externalApiClientService = new ExternalApiClientService(restTemplate);
    }

    @Test
    void forwardCardRequestToExternalApi_WithTestData() {
        NewCardRequest cardRequest = new NewCardRequest();
        cardRequest.setFirstName("Test");
        cardRequest.setLastName("User");
        cardRequest.setOib("12345678901");
        cardRequest.setStatus("PENDING");

        externalApiClientService.forwardCardRequestToExternalApi(cardRequest);

        // We're testing that the method runs without errors
        // Since the API is disabled, no actual HTTP calls are made
    }
}