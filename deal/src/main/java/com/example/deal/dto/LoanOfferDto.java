package com.example.deal.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoanOfferDto {

    @NotNull(message = "Идентификатор заявления не может быть нулевым")
    private UUID statementId;
    @NotNull(message = "Запрошенная сумма не может быть нулевой")
    private BigDecimal requestedAmount;
    @NotNull(message = "Общая сумма не может быть нулевой")
    private BigDecimal totalAmount;
    @NotNull(message = "Срок не может быть нулевым")
    private Integer term;

    private BigDecimal monthlyPayment;
    private BigDecimal rate;
    private Boolean insuranceEnabled;
    private Boolean salaryClient;
}
