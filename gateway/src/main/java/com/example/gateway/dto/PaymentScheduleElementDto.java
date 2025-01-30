package com.example.gateway.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentScheduleElementDto {

    @Positive(message = "Номер платежа должен быть положительным")
    private Integer number;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @PositiveOrZero(message = "Общая сумма платежа не может быть отрицательной")
    private BigDecimal totalPayment;

    @PositiveOrZero(message = "Сумма процентного платежа не может быть отрицательной")
    private BigDecimal interestPayment;

    @PositiveOrZero(message = "Сумма погашения основного долга не может быть отрицательной")
    private BigDecimal debtPayment;

    @PositiveOrZero(message = "Оставшийся долг не может быть отрицательным")
    private BigDecimal remainingDebt;
}