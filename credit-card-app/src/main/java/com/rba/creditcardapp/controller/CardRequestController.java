package com.rba.creditcardapp.controller;

import com.rba.creditcardapp.model.NewCardRequest;
import com.rba.creditcardapp.model.Client;
import com.rba.creditcardapp.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Card Requests", description = "API for card request management")
@Slf4j
public class CardRequestController {
    private final ClientService clientService;

    public CardRequestController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping("/card-requests")
    @Operation(summary = "Create a new card request",
            description = "Creates a new client and forwards data to external API")
    public ResponseEntity<Client> createCardRequest(@RequestBody NewCardRequest newCardRequest) {
        log.info("Received card request for: {} {}",
                newCardRequest.getFirstName(), newCardRequest.getLastName());

        Client client = clientService.registerClientFromCardRequest(newCardRequest);

        return ResponseEntity.status(201).body(client);
    }
}