package com.example.deal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class LoanStatementRequestDto {

    @NotNull(message = "Сумма кредита не должна быть нулевой")
    private BigDecimal amount;
    @NotNull(message = "Срок кредита не должен быть нулевым")
    private Integer term;
    @NotBlank(message = "Имя не должно быть пустым")
    private String firstName;
    @NotBlank(message = "Фамилия не должна быть пустым")
    private String lastName;
    private String middleName;
    @NotBlank(message = "Email не должен быть пустым")
    private String email;
    @NotNull(message = "Дата рождения не должна быть пустой")
    private LocalDate birthDate;
    @NotBlank(message = "Серия паспорта не должна быть пустой")
    private String passportSeries;
    @NotBlank(message = "Номер паспорта не должнен быть пустой")
    private String passportNumber;
}
