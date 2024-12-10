package com.example.calculator.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class LoanOfferDto {

    @NotNull(message = "StatementId не может быть нулевым")
    private UUID statementId;
    @NotNull(message = "Запрошенная сумма не может быть нулеввой")
    @DecimalMin(value = "0.0", inclusive = false, message = "Запрошенная сумма должна быть больше 0")
    private BigDecimal requestedAmount;
    @NotNull(message = "Общая сумма не может быть нулевой")
    @DecimalMin(value = "0.0", inclusive = false, message = "Общая сумма должна быть больше 0")
    private BigDecimal totalAmount;
    @NotNull(message = "Срок не может быть нулевым")
    @Min(value = 1, message = "Срок должен быть не менее 1 месяца")
    private Integer term;
    @NotNull(message = "Ежемесячный платеж не может быть нулевым")
    @DecimalMin(value = "0.0", inclusive = false, message = "Ежемесячный платеж должен быть больше 0")
    private BigDecimal monthlyPayment;
    @NotNull(message = "Ставка не может быть нулевой")
    @DecimalMin(value = "0.0", inclusive = false, message = "Ставка должна быть больше 0")
    private BigDecimal rate;
    @NotNull(message = "Статус страхования не может быть нулевым")
    private Boolean isInsuranceEnabled;
    @NotNull(message = "Статус зарплатного клиента не может быть нулевым")
    private Boolean isSalaryClient;


}
