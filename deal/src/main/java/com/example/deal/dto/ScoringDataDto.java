package com.example.deal.dto;

import com.example.deal.enums.Gender;
import com.example.deal.enums.MaritalStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "Сумма кредита", example = "500000")
    private BigDecimal amount;

    @NotNull(message = "Срок не может быть нулевым")
    @Positive(message = "Срок кредита должен быть положительным")
    @Schema(description = "Срок кредита в месяцах", example = "24")
    private Integer term;

    @NotEmpty(message = "Имя не может быть пустым")
    @Schema(description = "Имя клиента", example = "Иван")
    private String firstName;

    @NotEmpty(message = "Фамилия не может быть пустой")
    @Schema(description = "Фамилия клиента", example = "Иванов")
    private String lastName;

    @Schema(description = "Отчество клиента (если указано)", example = "Иванович")
    private String middleName;

    @NotNull(message = "Гендер не должен быть пустым")
    @Schema(description = "Пол клиента", example = "MALE")
    private Gender gender;

    @NotNull(message = "Дата рождения не должна быть пустой")
    @Past(message = "Дата рождения должна быть в прошлом")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Дата рождения клиента", example = "1985-05-15")
    private LocalDate birthDate;

    @NotEmpty(message = "Серия паспорта не должна быть пустой")
    @Pattern(regexp = "\\d{4}", message = "Серия паспорта должна состоять из 4 цифр")
    @Schema(description = "Серия паспорта клиента", example = "1234")
    private String passportSeries;

    @NotEmpty(message = "Номер паспорта не должен быть пустым")
    @Pattern(regexp = "\\d{6}", message = "Номер паспорта должен состоять из 6 цифр")
    @Schema(description = "Номер паспорта клиента", example = "123456")
    private String passportNumber;

    @NotNull(message = "Дата выдачи паспорта не должна быть пустой")
    @Past(message = "Дата выдачи паспорта должна быть в прошлом")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Дата выдачи паспорта", example = "2010-06-01")
    private LocalDate passportIssueDate;

    @NotEmpty(message = "Отделение выдачи паспорта не должно быть пустым")
    @Schema(description = "Отделение выдачи паспорта", example = "УФМС России по г. Москве")
    private String passportIssueBranch;

    @NotNull(message = "Семейное положение не должно быть пустым")
    @Schema(description = "Семейное положение клиента", example = "MARRIED")
    private MaritalStatus maritalStatus;

    @NotNull(message = "Количество иждивенцев не должно быть пустым")
    @PositiveOrZero(message = "Количество иждивенцев не может быть отрицательным")
    @Schema(description = "Количество иждивенцев", example = "2")
    private Integer dependentAmount;

    @NotNull(message = "Информация о занятости не должна быть пустой")
    @Schema(description = "Информация о занятости клиента")
    private EmploymentDto employment;

    @NotEmpty(message = "Номер счета не должен быть пустым")
    @Pattern(regexp = "\\d{20}", message = "Номер счета должен состоять из 20 цифр")
    @Schema(description = "Номер счета клиента", example = "40702810123456789012")
    private String accountNumber;

    @Schema(description = "Признак включения страхования", example = "true")
    private boolean isInsuranceEnabled;

    @Schema(description = "Признак клиента, получающего зарплату в банке", example = "true")
    private boolean isSalaryClient;
}