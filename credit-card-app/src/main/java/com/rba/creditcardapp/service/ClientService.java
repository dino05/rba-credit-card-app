package com.rba.creditcardapp.service;

import com.rba.creditcardapp.dto.ClientRequest;
import com.rba.creditcardapp.dto.ClientResponse;
import com.rba.creditcardapp.model.NewCardRequest;
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

    public ClientResponse registerClient(ClientRequest clientRequest) {
        log.info("Registering new client with OIB: {}", clientRequest.getOib());

        if (clientRepository.existsByOib(clientRequest.getOib())) {
            throw new IllegalArgumentException("Client with OIB " + clientRequest.getOib() + " already exists");
        }

        Client client = clientMapper.toEntity(clientRequest);
        Client savedClient = clientRepository.save(client);

        log.info("Successfully registered client with ID: {}", savedClient.getId());
        return clientMapper.toResponse(savedClient);
    }

    public Client registerClientFromCardRequest(NewCardRequest cardRequest) {
        log.info("Processing card request for: {} {}",
                cardRequest.getFirstName(), cardRequest.getLastName());

        validateCardRequest(cardRequest);

        if (clientRepository.existsByOib(cardRequest.getOib())) {
            throw new IllegalArgumentException("Client with OIB " + cardRequest.getOib() + " already exists");
        }

        Client client = createClientFromRequest(cardRequest);
        Client savedClient = clientRepository.save(client);

        externalApiClientService.forwardCardRequestToExternalApi(cardRequest);

        log.info("Successfully processed card request for OIB: {}", cardRequest.getOib());
        return savedClient;
    }

    public Optional<ClientResponse> findByOib(String oib) {
        log.debug("Searching for client with OIB: {}", oib);
        return clientRepository.findByOib(oib)
                .map(clientMapper::toResponse);
    }

    public Page<ClientResponse> findAll(Pageable pageable) {
        log.debug("Retrieving clients page: {}, size: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        return clientRepository.findAll(pageable)
                .map(clientMapper::toResponse);
    }

    public ClientResponse updateClientStatus(String oib, String status) {
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

    private void validateCardRequest(NewCardRequest cardRequest) {
        if (cardRequest.getOib() == null || cardRequest.getOib().length() != 11) {
            throw new IllegalArgumentException("OIB must be exactly 11 characters");
        }
        if (cardRequest.getFirstName() == null || cardRequest.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (cardRequest.getLastName() == null || cardRequest.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }
    }

    private Client createClientFromRequest(NewCardRequest cardRequest) {
        Client client = new Client();
        client.setFirstName(cardRequest.getFirstName());
        client.setLastName(cardRequest.getLastName());
        client.setOib(cardRequest.getOib());
        client.setCardStatus(cardRequest.getStatus() != null ? cardRequest.getStatus() : "PENDING");
        return client;
    }
}