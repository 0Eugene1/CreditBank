package com.example.calculator.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class CreditDto {

    private BigDecimal amount;
    private BigDecimal monthlyPayment;
    private BigDecimal rate;
    private BigDecimal psk;
    private Boolean isInsuranceEnabled;
    private Boolean isSalaryClient;
    private List<PaymentScheduleElementDto> paymentSchedule;
}
