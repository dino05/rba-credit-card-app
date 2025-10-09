package com.rba.creditcardapp.controller;

import com.rba.creditcardapp.dto.ClientRequestDto;
import com.rba.creditcardapp.dto.ClientResponseDto;
import com.rba.creditcardapp.dto.PageResponseDto;
import com.rba.creditcardapp.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/clients")
@Tag(name = "Clients", description = "API for client management")
@AllArgsConstructor
@Validated
public class ClientController {

    private final ClientService clientService;

    @PostMapping
    @Operation(summary = "Register a new client")
    public ResponseEntity<ClientResponseDto> registerClient(
            @Valid @RequestBody ClientRequestDto clientRequestDto) {
        ClientResponseDto response = clientService.registerClient(clientRequestDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{oib}")
    @Operation(summary = "Search for a client by OIB")
    public ResponseEntity<ClientResponseDto> getClientByOib(
            @PathVariable @jakarta.validation.constraints.Size(min = 11, max = 11) String oib) {
        return clientService.findByOib(oib)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Get clients with pagination")
    public ResponseEntity<PageResponseDto<ClientResponseDto>> getAllClients(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "firstName") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String direction) {

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ClientResponseDto> clientPage = clientService.findAll(pageable);

        PageResponseDto<ClientResponseDto> response = new PageResponseDto<>(clientPage);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{oib}")
    @Operation(summary = "Delete a client by OIB")
    public ResponseEntity<Void> deleteClient(
            @PathVariable @jakarta.validation.constraints.Size(min = 11, max = 11) String oib) {
        clientService.deleteByOib(oib);
        return ResponseEntity.ok().build();
    }
}