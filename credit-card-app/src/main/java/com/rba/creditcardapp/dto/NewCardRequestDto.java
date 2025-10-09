package com.rba.creditcardapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewCardRequestDto {
    @NotBlank(message = "First name is mandatory")
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    private String lastName;

    @NotBlank(message = "OIB is mandatory")
    @Size(min = 11, max = 11, message = "OIB must be exactly 11 characters")
    private String oib;

    private String status;

    public static NewCardRequestDto fromClient(ClientResponseDto client) {
        return NewCardRequestDto.builder()
                .firstName(client.getFirstName())
                .lastName(client.getLastName())
                .oib(client.getOib())
                .status(client.getCardStatus())
                .build();
    }
}