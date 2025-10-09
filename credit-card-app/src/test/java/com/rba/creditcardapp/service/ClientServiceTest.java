package com.rba.creditcardapp.service;

import com.rba.creditcardapp.dto.ClientRequestDto;
import com.rba.creditcardapp.dto.ClientResponseDto;
import com.rba.creditcardapp.utils.ClientMapper;
import com.rba.creditcardapp.model.NewCardRequest;
import com.rba.creditcardapp.model.Client;
import com.rba.creditcardapp.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ExternalApiClientService externalApiClientService;

    @Mock
    private ClientMapper clientMapper;

    private ClientService clientService;

    @BeforeEach
    void setUp() {
        clientService = new ClientService(clientRepository, clientMapper, externalApiClientService);
    }

    @Test
    void registerClient_Success() {
        ClientRequestDto clientRequest = createTestClientRequest();
        Client clientEntity = createTestClient();
        Client savedClient = createTestClient();
        savedClient.setId(1L);
        ClientResponseDto expectedResponse = createTestClientResponse();

        when(clientRepository.existsByOib("12345678901")).thenReturn(false);
        when(clientMapper.toEntity(clientRequest)).thenReturn(clientEntity);
        when(clientRepository.save(clientEntity)).thenReturn(savedClient);
        when(clientMapper.toResponse(savedClient)).thenReturn(expectedResponse);

        ClientResponseDto result = clientService.registerClient(clientRequest);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John", result.getFirstName());
        assertEquals("12345678901", result.getOib());

        verify(clientRepository).existsByOib("12345678901");
        verify(clientMapper).toEntity(clientRequest);
        verify(clientRepository).save(clientEntity);
        verify(clientMapper).toResponse(savedClient);
    }

    @Test
    void registerClient_ClientAlreadyExists() {
        ClientRequestDto clientRequest = createTestClientRequest();

        when(clientRepository.existsByOib("12345678901")).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> clientService.registerClient(clientRequest));

        verify(clientRepository).existsByOib("12345678901");
        verify(clientRepository, never()).save(any(Client.class));
        verify(clientMapper, never()).toResponse(any());
    }

    @Test
    void findByOib_ClientFound() {
        String oib = "12345678901";
        Client client = createTestClient();
        client.setId(1L);
        ClientResponseDto expectedResponse = createTestClientResponse();

        when(clientRepository.findByOib(oib)).thenReturn(Optional.of(client));
        when(clientMapper.toResponse(client)).thenReturn(expectedResponse);

        Optional<ClientResponseDto> result = clientService.findByOib(oib);

        assertTrue(result.isPresent());
        assertEquals("John", result.get().getFirstName());
        assertEquals(oib, result.get().getOib());

        verify(clientRepository).findByOib(oib);
        verify(clientMapper).toResponse(client);
    }

    @Test
    void findByOib_ClientNotFound() {
        String oib = "12345678901";

        when(clientRepository.findByOib(oib)).thenReturn(Optional.empty());

        Optional<ClientResponseDto> result = clientService.findByOib(oib);

        assertTrue(result.isEmpty());

        verify(clientRepository).findByOib(oib);
        verify(clientMapper, never()).toResponse(any());
    }

    @Test
    void deleteByOib_Success() {
        String oib = "12345678901";

        when(clientRepository.existsByOib(oib)).thenReturn(true);
        doNothing().when(clientRepository).deleteByOib(oib);

        clientService.deleteByOib(oib);

        verify(clientRepository).existsByOib(oib);
        verify(clientRepository).deleteByOib(oib);
    }

    @Test
    void deleteByOib_ClientNotFound() {
        String oib = "12345678901";

        when(clientRepository.existsByOib(oib)).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> clientService.deleteByOib(oib));

        verify(clientRepository).existsByOib(oib);
        verify(clientRepository, never()).deleteByOib(oib);
    }

    @Test
    void findAll_WithPagination() {
        Pageable pageable = PageRequest.of(0, 10);
        Client client = createTestClient();
        List<Client> clients = List.of(client);
        Page<Client> clientPage = new PageImpl<>(clients, pageable, 1);
        ClientResponseDto clientResponse = createTestClientResponse();
        Page<ClientResponseDto> expectedPage = new PageImpl<>(List.of(clientResponse), pageable, 1);

        when(clientRepository.findAll(pageable)).thenReturn(clientPage);
        when(clientMapper.toResponse(client)).thenReturn(clientResponse);

        Page<ClientResponseDto> result = clientService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals("John", result.getContent().get(0).getFirstName());

        verify(clientRepository).findAll(pageable);
        verify(clientMapper).toResponse(client);
    }

    @Test
    void updateClientStatus_Success() {
        String oib = "12345678901";
        String newStatus = "APPROVED";
        Client existingClient = createTestClient();
        existingClient.setId(1L);
        Client updatedClient = createTestClient();
        updatedClient.setCardStatus(newStatus);
        ClientResponseDto expectedResponse = createTestClientResponse();
        expectedResponse.setCardStatus(newStatus);

        when(clientRepository.findByOib(oib)).thenReturn(Optional.of(existingClient));
        when(clientRepository.save(existingClient)).thenReturn(updatedClient);
        when(clientMapper.toResponse(updatedClient)).thenReturn(expectedResponse);

        ClientResponseDto result = clientService.updateClientStatus(oib, newStatus);

        assertNotNull(result);
        assertEquals(newStatus, result.getCardStatus());
        assertEquals("John", result.getFirstName());

        verify(clientRepository).findByOib(oib);
        verify(clientRepository).save(existingClient);
        verify(clientMapper).toResponse(updatedClient);
    }

    @Test
    void updateClientStatus_ClientNotFound() {
        String oib = "12345678901";
        String newStatus = "APPROVED";

        when(clientRepository.findByOib(oib)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> clientService.updateClientStatus(oib, newStatus));

        verify(clientRepository).findByOib(oib);
        verify(clientRepository, never()).save(any());
        verify(clientMapper, never()).toResponse(any());
    }

    @Test
    void updateClientStatus_InvalidOib() {
        String invalidOib = "123";
        String newStatus = "APPROVED";

        assertThrows(IllegalArgumentException.class,
                () -> clientService.updateClientStatus(invalidOib, newStatus));

        verify(clientRepository, never()).findByOib(any());
        verify(clientRepository, never()).save(any());
    }

    @Test
    void updateClientStatus_InvalidStatus() {
        String oib = "12345678901";
        String invalidStatus = "";

        assertThrows(IllegalArgumentException.class,
                () -> clientService.updateClientStatus(oib, invalidStatus));

        verify(clientRepository, never()).findByOib(any());
        verify(clientRepository, never()).save(any());
    }

    private ClientRequestDto createTestClientRequest() {
        ClientRequestDto request = new ClientRequestDto();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setOib("12345678901");
        request.setCardStatus("PENDING");
        return request;
    }

    private Client createTestClient() {
        Client client = new Client();
        client.setId(1L);
        client.setFirstName("John");
        client.setLastName("Doe");
        client.setOib("12345678901");
        client.setCardStatus("PENDING");
        client.setCreatedAt(LocalDateTime.now());
        client.setUpdatedAt(LocalDateTime.now());
        return client;
    }

    private ClientResponseDto createTestClientResponse() {
        ClientResponseDto response = new ClientResponseDto();
        response.setId(1L);
        response.setFirstName("John");
        response.setLastName("Doe");
        response.setOib("12345678901");
        response.setCardStatus("PENDING");
        response.setCreatedAt(LocalDateTime.now());
        response.setUpdatedAt(LocalDateTime.now());
        return response;
    }
}