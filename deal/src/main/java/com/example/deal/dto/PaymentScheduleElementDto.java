package com.example.deal.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "Номер платежа в графике", example = "1")
    private Integer number;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Дата платежа", example = "2024-01-01")
    private LocalDate date;

    @PositiveOrZero(message = "Общая сумма платежа не может быть отрицательной")
    @Schema(description = "Общая сумма платежа", example = "15000.00")
    private BigDecimal totalPayment;

    @PositiveOrZero(message = "Сумма процентного платежа не может быть отрицательной")
    @Schema(description = "Сумма процентного платежа", example = "5000.00")
    private BigDecimal interestPayment;

    @PositiveOrZero(message = "Сумма погашения основного долга не может быть отрицательной")
    @Schema(description = "Сумма погашения основного долга", example = "10000.00")
    private BigDecimal debtPayment;

    @PositiveOrZero(message = "Оставшийся долг не может быть отрицательным")
    @Schema(description = "Оставшийся долг после платежа", example = "400000.00")
    private BigDecimal remainingDebt;
}