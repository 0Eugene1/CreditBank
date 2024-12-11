package com.example.deal.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)  // Игнорирует неизвестные поля
public class CreditDto {

    @NotNull(message = "Сумма кредита не должна быть нулевой")
    private BigDecimal amount;
    @NotNull(message = "Срок кредита не должен быть нулевым")
    @JsonProperty("term")
    private Integer term;
    @NotNull(message = "Ежемесячный платеж не должен быть нулевым")
    private BigDecimal monthlyPayment;
    @NotNull(message = "Ставка не должна быть нулевой")
    private BigDecimal rate;
    @NotNull(message = "PSK не должен быть нулевым")
    private BigDecimal psk;
    private boolean isInsuranceEnabled;
    private boolean isSalaryClient;
    private List<PaymentScheduleElementDto> paymentSchedule;

}
