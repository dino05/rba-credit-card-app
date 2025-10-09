package com.rba.creditcardapp.service;

import com.rba.creditcardapp.api.NewCardRequestApi;
import com.rba.creditcardapp.dto.NewCardRequestDto;
import com.rba.creditcardapp.model.NewCardRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExternalApiClientServiceTest {

    @Mock
    private NewCardRequestApi newCardRequestApi;

    private ExternalApiClientService externalApiClientService;

    @BeforeEach
    void setUp() {
        externalApiClientService = new ExternalApiClientService(newCardRequestApi);
        ReflectionTestUtils.setField(externalApiClientService, "externalApiEnabled", true);
    }

    @Test
    void forwardClientToExternalApi_Success() {
        NewCardRequestDto requestDto = createTestRequestDto();
        ResponseEntity<Void> successResponse = ResponseEntity.status(HttpStatus.CREATED).build();

        when(newCardRequestApi.apiV1CardRequestPost(any(NewCardRequest.class)))
                .thenReturn(successResponse);

        boolean result = externalApiClientService.forwardClientToExternalApi(requestDto);

        assertTrue(result);
        verify(newCardRequestApi).apiV1CardRequestPost(any(NewCardRequest.class));
    }

    @Test
    void forwardClientToExternalApi_ExternalApiReturnsError() {
        NewCardRequestDto requestDto = createTestRequestDto();
        ResponseEntity<Void> errorResponse = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        when(newCardRequestApi.apiV1CardRequestPost(any(NewCardRequest.class)))
                .thenReturn(errorResponse);

        boolean result = externalApiClientService.forwardClientToExternalApi(requestDto);

        assertFalse(result);
        verify(newCardRequestApi).apiV1CardRequestPost(any(NewCardRequest.class));
    }

    @Test
    void forwardClientToExternalApi_NetworkError() {
        NewCardRequestDto requestDto = createTestRequestDto();

        when(newCardRequestApi.apiV1CardRequestPost(any(NewCardRequest.class)))
                .thenThrow(new RestClientException("Connection refused"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> externalApiClientService.forwardClientToExternalApi(requestDto));

        assertTrue(exception.getMessage().contains("Failed to call external API"));
        verify(newCardRequestApi).apiV1CardRequestPost(any(NewCardRequest.class));
    }

    @Test
    void forwardClientToExternalApi_UnexpectedError() {
        NewCardRequestDto requestDto = createTestRequestDto();

        when(newCardRequestApi.apiV1CardRequestPost(any(NewCardRequest.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> externalApiClientService.forwardClientToExternalApi(requestDto));

        assertTrue(exception.getMessage().contains("Failed to call external API"));
        verify(newCardRequestApi).apiV1CardRequestPost(any(NewCardRequest.class));
    }

    @Test
    void forwardClientToExternalApi_ConvertsDtoToApiModelCorrectly() {
        NewCardRequestDto requestDto = createTestRequestDto();
        ResponseEntity<Void> successResponse = ResponseEntity.status(HttpStatus.CREATED).build();

        when(newCardRequestApi.apiV1CardRequestPost(any(NewCardRequest.class)))
                .thenReturn(successResponse);

        externalApiClientService.forwardClientToExternalApi(requestDto);

        verify(newCardRequestApi).apiV1CardRequestPost(argThat(apiRequest ->
                apiRequest.getFirstName().equals("John") &&
                        apiRequest.getLastName().equals("Doe") &&
                        apiRequest.getOib().equals("12345678901") &&
                        apiRequest.getStatus().equals("PENDING")
        ));
    }

    @Test
    void forwardClientToExternalApi_SimulationMode() {
        ReflectionTestUtils.setField(externalApiClientService, "externalApiEnabled", false);

        NewCardRequestDto requestDto = createTestRequestDto();

        boolean result = externalApiClientService.forwardClientToExternalApi(requestDto);

        assertTrue(result);
        verifyNoInteractions(newCardRequestApi);
    }

    private NewCardRequestDto createTestRequestDto() {
        return NewCardRequestDto.builder()
                .firstName("John")
                .lastName("Doe")
                .oib("12345678901")
                .status("PENDING")
                .build();
    }
}