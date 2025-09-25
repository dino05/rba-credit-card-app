package com.rba.creditcardapp.kafka;

import com.rba.creditcardapp.dto.CardStatusUpdate;
import com.rba.creditcardapp.service.ClientService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardStatusConsumerTest {

    @Mock
    private ClientService clientService;

    @InjectMocks
    private CardStatusConsumer cardStatusConsumer;

    @Test
    void receiveCardStatusUpdate_Success() {
        CardStatusUpdate statusUpdate = createValidStatusUpdate();

        cardStatusConsumer.receiveCardStatusUpdate(statusUpdate);

        verify(clientService).updateClientStatus("12345678901", "APPROVED");
    }

    @Test
    void receiveCardStatusUpdate_InvalidOib() {
        CardStatusUpdate statusUpdate = new CardStatusUpdate("123", "APPROVED", "Test reason");

        cardStatusConsumer.receiveCardStatusUpdate(statusUpdate);

        verify(clientService, never()).updateClientStatus(anyString(), anyString());
    }

    @Test
    void receiveCardStatusUpdate_NullStatus() {
        CardStatusUpdate statusUpdate = new CardStatusUpdate();
        statusUpdate.setOib("12345678901");
        statusUpdate.setStatus(null);
        statusUpdate.setUpdateReason("Test reason");

        cardStatusConsumer.receiveCardStatusUpdate(statusUpdate);

        verify(clientService, never()).updateClientStatus(anyString(), anyString());
    }

    @Test
    void receiveCardStatusUpdate_InvalidStatusValue() {
        CardStatusUpdate statusUpdate = new CardStatusUpdate("12345678901", "INVALID_STATUS", "Test reason");

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
        CardStatusUpdate statusUpdate = createValidStatusUpdate();

        doThrow(new RuntimeException("Service error"))
                .when(clientService)
                .updateClientStatus("12345678901", "APPROVED");

        // Should not throw exception to the caller (should be caught and logged)
        cardStatusConsumer.receiveCardStatusUpdate(statusUpdate);

        verify(clientService).updateClientStatus("12345678901", "APPROVED");
    }

    private CardStatusUpdate createValidStatusUpdate() {
        return new CardStatusUpdate("12345678901", "APPROVED", "Card production completed");
    }
}