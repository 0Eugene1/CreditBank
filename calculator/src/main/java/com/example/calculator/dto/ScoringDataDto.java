package com.example.calculator.dto;

import com.example.calculator.enums.GenderEnum;
import com.example.calculator.enums.MaritalStatusEnum;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class ScoringDataDto {

    @NotNull(message = "Сумма не может быть нулевой")
    @DecimalMin(value = "20000.00", message = "Сумма должна быть не менее 20000")
    @DecimalMax(value = "1000000.00", message = "Сумма должн быть максимум 1000000")
    private BigDecimal amount;
    @NotNull(message = "Срок не может быть нулевым")
    @Min(value = 6, message = "Срок должен быть не менее 6 месяцев")
    @Max(value = 120, message = "Срок должен быть максимум 120 месяцев")
    private Integer term;
    @NotEmpty(message = "Поле имя не должно быть пустым")
    private String firstName;
    @NotEmpty(message = "Поле фамилия не должно быть пустым")
    private String lastName;
    private String middleName;
    private GenderEnum gender;
    @NotNull(message = "Поле минимальный возраст не может быть пустым")
    private LocalDate birthDate;
    @NotEmpty(message = "Серия паспорта не может быть пустой")
    private String passportSeries;
    @NotEmpty(message = "Номер паспорта не может быть пустым")
    private String passportNumber;
    private LocalDate passportIssueDate;
    private String passportIssueBranch;
    private MaritalStatusEnum maritalStatus;
    private Integer dependentAmount;
    private EmploymentDto employment;
    private String accountNumber;
    private Boolean isInsuranceEnabled;
    private Boolean isSalaryClient;

}
