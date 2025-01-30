package com.example.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatementDto {
    private UUID statementId;
    private String clientName; // Пример поля из Client
    private String creditType; // Пример поля из Credit
    private String status;
    private LocalDateTime creationDate;
}

