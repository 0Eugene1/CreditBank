package com.example.deal.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoanOfferDto {

    @NotNull(message = "Идентификатор заявления не должен быть нулевым")
    private UUID statementId;

    @NotNull(message = "Запрошенная сумма не должна быть нулевой")
    @Positive(message = "Запрошенная сумма должна быть положительной")
    private BigDecimal requestedAmount;

    @NotNull(message = "Общая сумма не должна быть нулевой")
    @Positive(message = "Общая сумма должна быть положительной")
    private BigDecimal totalAmount;

    @NotNull(message = "Срок кредита не должен быть нулевым")
    @Positive(message = "Срок кредита должен быть положительным числом")
    private Integer term;

    @NotNull(message = "Ежемесячный платеж не должен быть нулевым.")
    @Positive(message = "Ежемесячный платеж должен быть положительным")
    private BigDecimal monthlyPayment;

    @NotNull(message = "Ставка не должна быть нулевой.")
    @DecimalMin(value = "0.0", message = "Ставка должна быть не меньше 0%")
    @DecimalMax(value = "100.0", message = "Ставка должна быть не больше 100%")
    private BigDecimal rate;

    @NotNull(message = "Поле insuranceEnabled не может быть null")
    @JsonProperty("insuranceEnabled")
    private Boolean insuranceEnabled;

    @NotNull(message = "Поле salaryClient не может быть null")
    @JsonProperty("salaryClient")
    private Boolean salaryClient;
}
