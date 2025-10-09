package com.rba.creditcardapp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardStatusUpdateDto {
    private String oib;
    private String status;
    private String updateReason;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    public CardStatusUpdateDto(String oib, String status, String updateReason) {
        this.oib = oib;
        this.status = status;
        this.updateReason = updateReason;
        this.timestamp = LocalDateTime.now();
    }
}
