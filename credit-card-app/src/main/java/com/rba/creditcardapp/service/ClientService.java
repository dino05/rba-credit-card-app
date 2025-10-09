package com.rba.creditcardapp.service;

import com.rba.creditcardapp.dto.ClientRequestDto;
import com.rba.creditcardapp.dto.ClientResponseDto;
import com.rba.creditcardapp.dto.NewCardRequestDto;
import com.rba.creditcardapp.utils.ClientMapper;
import com.rba.creditcardapp.model.Client;
import com.rba.creditcardapp.repository.ClientRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@Slf4j
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final ExternalApiClientService externalApiClientService;

    public ClientService(ClientRepository clientRepository,
                         ClientMapper clientMapper,
                         ExternalApiClientService externalApiClientService) {
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
        this.externalApiClientService = externalApiClientService;
    }

    public ClientResponseDto registerClient(ClientRequestDto clientRequestDto) {
        log.info("Registering new client with OIB: {}", clientRequestDto.getOib());

        if (clientRepository.existsByOib(clientRequestDto.getOib())) {
            throw new IllegalArgumentException("Client with OIB " + clientRequestDto.getOib() + " already exists");
        }

        Client client = clientMapper.toEntity(clientRequestDto);
        Client savedClient = clientRepository.save(client);

        log.info("Successfully registered client with ID: {}", savedClient.getId());
        return clientMapper.toResponse(savedClient);
    }

    public Optional<ClientResponseDto> findByOib(String oib) {
        log.debug("Searching for client with OIB: {}", oib);

        Optional<Client> client = clientRepository.findByOib(oib);

        if (client.isPresent()) {
            ClientResponseDto clientResponseDto = clientMapper.toResponse(client.get());

            forwardClientToExternalApiAsync(clientResponseDto);

            return Optional.of(clientResponseDto);
        } else {
            return Optional.empty();
        }
    }

    private void forwardClientToExternalApiAsync(ClientResponseDto client) {
        try {
            NewCardRequestDto cardRequest = NewCardRequestDto.fromClient(client);

            new Thread(() -> {
                try {
                    externalApiClientService.forwardClientToExternalApi(cardRequest);
                    log.info("Successfully auto-forwarded client OIB: {} to external API", client.getOib());
                } catch (Exception e) {
                    log.warn("Failed to auto-forward client OIB: {} to external API: {}",
                            client.getOib(), e.getMessage());
                }
            }).start();

        } catch (Exception e) {
            log.warn("Error preparing to auto-forward client OIB: {} to external API: {}",
                    client.getOib(), e.getMessage());
        }
    }

    public Page<ClientResponseDto> findAll(Pageable pageable) {
        log.debug("Retrieving clients page: {}, size: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        return clientRepository.findAll(pageable)
                .map(clientMapper::toResponse);
    }

    public ClientResponseDto updateClientStatus(String oib, String status) {
        log.info("Updating status for client with OIB: {} to {}", oib, status);

        validateOib(oib);
        validateStatus(status);

        Client client = clientRepository.findByOib(oib)
                .orElseThrow(() -> new IllegalArgumentException("Client with OIB " + oib + " not found"));

        client.setCardStatus(status);
        Client updatedClient = clientRepository.save(client);

        log.info("Successfully updated status for client with OIB: {}", oib);
        return clientMapper.toResponse(updatedClient);
    }

    public void deleteByOib(String oib) {
        log.info("Deleting client with OIB: {}", oib);

        if (!clientRepository.existsByOib(oib)) {
            throw new IllegalArgumentException("Client with OIB " + oib + " not found");
        }

        clientRepository.deleteByOib(oib);
        log.info("Successfully deleted client with OIB: {}", oib);
    }

    private void validateOib(String oib) {
        if (oib == null || !oib.matches("\\d{11}")) {
            throw new IllegalArgumentException("OIB must be exactly 11 digits");
        }
    }

    private void validateStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be null or empty");
        }
    }
}