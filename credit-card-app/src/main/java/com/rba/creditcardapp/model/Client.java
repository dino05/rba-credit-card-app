package com.rba.creditcardapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "clients", uniqueConstraints = {
        @UniqueConstraint(columnNames = "oib")
})
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "First name is mandatory")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotBlank(message = "OIB is mandatory")
    @Size(min = 11, max = 11, message = "OIB must be exactly 11 characters")
    @Column(nullable = false, unique = true)
    private String oib;

    @NotBlank(message = "Card status is mandatory")
    @Column(name = "card_status", nullable = false)
    private String cardStatus;

    @CreationTimestamp
    @Column(name = "instm", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updtm")
    private LocalDateTime updatedAt;
}