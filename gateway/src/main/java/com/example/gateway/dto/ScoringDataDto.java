package com.example.gateway.dto;

import com.example.gateway.enums.Gender;
import com.example.gateway.enums.MaritalStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
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
public class ScoringDataDto {

    @NotNull(message = "Сумма не может быть нулевой")
    @Positive(message = "Сумма должна быть положительной")
    private BigDecimal amount;

    @NotNull(message = "Срок не может быть нулевым")
    @Positive(message = "Срок кредита должен быть положительным")
    private Integer term;

    @NotEmpty(message = "Имя не может быть пустым")
    private String firstName;

    @NotEmpty(message = "Фамилия не может быть пустой")
    private String lastName;

    private String middleName;

    @NotNull(message = "Гендер не должен быть пустым")
    private Gender gender;

    @NotNull(message = "Дата рождения не должна быть пустой")
    @Past(message = "Дата рождения должна быть в прошлом")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    @NotEmpty(message = "Серия паспорта не должна быть пустой")
    @Pattern(regexp = "\\d{4}", message = "Серия паспорта должна состоять из 4 цифр")
    private String passportSeries;

    @NotEmpty(message = "Номер паспорта не должен быть пустым")
    @Pattern(regexp = "\\d{6}", message = "Номер паспорта должен состоять из 6 цифр")
    private String passportNumber;

    @NotNull(message = "Дата выдачи паспорта не должна быть пустой")
    @Past(message = "Дата выдачи паспорта должна быть в прошлом")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate passportIssueDate;

    @NotEmpty(message = "Отделение выдачи паспорта не должно быть пустым")
    private String passportIssueBranch;

    @NotNull(message = "Семейное положение не должно быть пустым")
    private MaritalStatus maritalStatus;

    @NotNull(message = "Количество иждивенцев не должно быть пустым")
    @PositiveOrZero(message = "Количество иждивенцев не может быть отрицательным")
    private Integer dependentAmount;

    @NotNull(message = "Информация о занятости не должна быть пустой")
    private EmploymentDto employment;

    @NotEmpty(message = "Номер счета не должен быть пустым")
    @Pattern(regexp = "\\d{20}", message = "Номер счета должен состоять из 20 цифр")
    private String accountNumber;

    private boolean isInsuranceEnabled;

    private boolean isSalaryClient;
}