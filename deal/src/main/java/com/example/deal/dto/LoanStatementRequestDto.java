package com.example.deal.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
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
@JsonIgnoreProperties(ignoreUnknown = true)  // Игнорирует неизвестные поля

public class LoanStatementRequestDto {

    @NotNull(message = "Сумма кредита не должна быть нулевой")
    @Positive(message = "Сумма кредита должна быть положительной")
    private BigDecimal amount;

    @NotNull(message = "Срок кредита не должен быть нулевым")
    @Positive(message = "Срок кредита должен быть положительным числом")
    private Integer term;

    @NotEmpty(message = "Имя не должно быть пустым")
    private String firstName;

    @NotEmpty(message = "Фамилия не должна быть пустой")
    private String lastName;

    private String middleName; // Отчество не обязательно

    @NotEmpty(message = "Email не должен быть пустым")
    @Email(message = "Некорректный формат Email")
    private String email;

    @NotNull(message = "Дата рождения не должна быть пустой")
    private LocalDate birthDate;

    @NotEmpty(message = "Серия паспорта не должна быть пустой")
    @Pattern(regexp = "\\d{4}", message = "Серия паспорта должна состоять из 4 цифр")
    private String passportSeries;

    @NotEmpty(message = "Номер паспорта не должен быть пустым")
    @Pattern(regexp = "\\d{6}", message = "Номер паспорта должна состоять из 6 цифр")
    private String passportNumber;
}