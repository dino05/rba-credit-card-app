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
public class CardStatusUpdate {
    private String oib;
    private String status;
    private String updateReason;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    public CardStatusUpdate(String oib, String status, String updateReason) {
        this.oib = oib;
        this.status = status;
        this.updateReason = updateReason;
        this.timestamp = LocalDateTime.now();
    }

    public String getOib() { return oib; }
    public void setOib(String oib) { this.oib = oib; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getUpdateReason() { return updateReason; }
    public void setUpdateReason(String updateReason) { this.updateReason = updateReason; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return "CardStatusUpdate{" +
                "oib='" + oib + '\'' +
                ", status='" + status + '\'' +
                ", updateReason='" + updateReason + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
