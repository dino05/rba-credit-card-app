package com.rba.creditcardapp.utils;

import com.rba.creditcardapp.dto.ClientRequest;
import com.rba.creditcardapp.dto.ClientResponse;
import com.rba.creditcardapp.model.Client;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {

    public ClientResponse toResponse(Client client) {
        if (client == null) return null;

        ClientResponse response = new ClientResponse();
        response.setId(client.getId());
        response.setFirstName(client.getFirstName());
        response.setLastName(client.getLastName());
        response.setOib(client.getOib());
        response.setCardStatus(client.getCardStatus());
        response.setCreatedAt(client.getCreatedAt());
        response.setUpdatedAt(client.getUpdatedAt());
        return response;
    }

    public Client toEntity(ClientRequest request) {
        if (request == null) return null;

        Client client = new Client();
        client.setFirstName(request.getFirstName());
        client.setLastName(request.getLastName());
        client.setOib(request.getOib());
        client.setCardStatus(request.getCardStatus() != null ?
                request.getCardStatus() : "PENDING");
        return client;
    }
}