package com.rba.creditcardapp.kafka;

import com.rba.creditcardapp.dto.CardStatusUpdateDto;
import com.rba.creditcardapp.service.ClientService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=",
        "spring.kafka.consumer.auto-startup=false"
})
class CardStatusConsumerTest {

    @Mock
    private ClientService clientService;

    @InjectMocks
    private CardStatusConsumer cardStatusConsumer;

    @Test
    void receiveCardStatusUpdate_Success() {
        CardStatusUpdateDto statusUpdate = createValidStatusUpdate();

        cardStatusConsumer.receiveCardStatusUpdate(statusUpdate);

        verify(clientService).updateClientStatus("12345678901", "APPROVED");
    }

    @Test
    void receiveCardStatusUpdate_InvalidOib() {
        CardStatusUpdateDto statusUpdate = new CardStatusUpdateDto("123", "APPROVED", "Test reason");

        cardStatusConsumer.receiveCardStatusUpdate(statusUpdate);

        verify(clientService, never()).updateClientStatus(anyString(), anyString());
    }

    @Test
    void receiveCardStatusUpdate_NullStatus() {
        CardStatusUpdateDto statusUpdate = new CardStatusUpdateDto();
        statusUpdate.setOib("12345678901");
        statusUpdate.setStatus(null);
        statusUpdate.setUpdateReason("Test reason");

        cardStatusConsumer.receiveCardStatusUpdate(statusUpdate);

        verify(clientService, never()).updateClientStatus(anyString(), anyString());
    }

    @Test
    void receiveCardStatusUpdate_InvalidStatusValue() {
        CardStatusUpdateDto statusUpdate = new CardStatusUpdateDto("12345678901", "INVALID_STATUS", "Test reason");

        cardStatusConsumer.receiveCardStatusUpdate(statusUpdate);

        verify(clientService, never()).updateClientStatus(anyString(), anyString());
    }

    @Test
    void receiveCardStatusUpdate_NullUpdate() {
        cardStatusConsumer.receiveCardStatusUpdate(null);
        verify(clientService, never()).updateClientStatus(anyString(), anyString());
    }

    @Test
    void receiveCardStatusUpdate_ServiceThrowsException() {
        CardStatusUpdateDto statusUpdate = createValidStatusUpdate();

        doThrow(new RuntimeException("Service error"))
                .when(clientService)
                .updateClientStatus("12345678901", "APPROVED");

        cardStatusConsumer.receiveCardStatusUpdate(statusUpdate);

        verify(clientService).updateClientStatus("12345678901", "APPROVED");
    }

    private CardStatusUpdateDto createValidStatusUpdate() {
        return new CardStatusUpdateDto("12345678901", "APPROVED", "Card production completed");
    }
}