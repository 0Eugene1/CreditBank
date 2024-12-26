package com.example.statement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanOfferDto {

    @Schema(description = "Идентификатор заявления", example = "123456789123")
    @NotNull(message = "statementId не должно быть пустым")
    private UUID statementId;
    @Schema(description = "Запрошенная сумма платежа", example = "20000.00")
    @NotNull(message = "requestedAmount не должно быть нулевым")
    private BigDecimal requestedAmount;
    @Schema(description = "Общая сумма платежа", example = "50000.00")
    @NotNull(message = "totalAmount не должно быть нулевым")
    private BigDecimal totalAmount;
    @Schema(description = "Срок кредита", example = "24")
    @NotNull(message = "term не должен быть пустым")
    private Integer term;
    @Schema(description = "Ежемесячный платеж", example = "1525.54")
    @NotNull(message = "monthlyPayment не должно быть нулевым")
    private BigDecimal monthlyPayment;
    @Schema(description = "Ставка кредита", example = "10.0")
    @NotNull(message = "rate не должно быть нулевым")
    private BigDecimal rate;

    private boolean isInsuranceEnabled;
    private boolean isSalaryClient;

}
