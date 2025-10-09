package com.rba.creditcardapp.utils;

import com.rba.creditcardapp.dto.ClientRequestDto;
import com.rba.creditcardapp.dto.ClientResponseDto;
import com.rba.creditcardapp.model.Client;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {

    public ClientResponseDto toResponse(Client client) {
        if (client == null) return null;

        return ClientResponseDto.builder()
                .id(client.getId())
                .firstName(client.getFirstName())
                .lastName(client.getLastName())
                .oib(client.getOib())
                .cardStatus(client.getCardStatus())
                .createdAt(client.getCreatedAt())
                .updatedAt(client.getUpdatedAt())
                .build();
    }

    public Client toEntity(ClientRequestDto request) {
        if (request == null) return null;

        return Client.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .oib(request.getOib())
                .cardStatus(request.getCardStatus() != null ? request.getCardStatus() : "PENDING")
                .build();
    }
}