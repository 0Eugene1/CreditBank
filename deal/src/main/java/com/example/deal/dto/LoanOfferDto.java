package com.example.deal.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class LoanOfferDto {

    @NotNull(message = "Идентификатор заявления не должен быть нулевым")
    private UUID statementId;
    @NotNull(message = "Запрошенная сумма не должна быть нулевой")
    private BigDecimal requestedAmount;
    @NotNull(message = "Общая сумма не должна быть нулевой")
    private BigDecimal totalAmount;
    @NotNull(message = "Срок кредита не должен быть нулевым")
    private Integer term;
    @NotNull(message = "Ежемесячный платеж не должен быть нулевым.")
    private BigDecimal monthlyPayment;
    @NotNull(message = "Ставка не должна быть нулевой.")
    private BigDecimal rate;
    private boolean isInsuranceEnabled;
    private boolean isSalaryClient;
}
