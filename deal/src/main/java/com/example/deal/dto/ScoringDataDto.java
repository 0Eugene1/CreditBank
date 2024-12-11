package com.example.deal.dto;

import com.example.deal.enums.Gender;
import com.example.deal.enums.MaritalStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
    private BigDecimal amount;
    @NotNull(message = "Срок не может быть нулевым")
    private Integer term;
    @NotEmpty(message = "Имя не может быть пустым")
    private String firstName;
    @NotEmpty(message = "Фамилия не может быть пустой")
    private String lastName;
    private String middleName;
    @NotNull(message = "Гендер не должен быть пустым")
    private Gender gender;
    @NotNull(message = "Дата рождения не должна быть пустой")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    @NotEmpty(message = "Серия паспорта не должна быть пустой")
    private String passportSeries;
    @NotEmpty(message = "Номер паспорта не должен быть пустым")
    private String passportNumber;
    private LocalDate passportIssueDate;
    private String passportIssueBranch;
    private MaritalStatus maritalStatus;
    private Integer dependentAmount;
    private EmploymentDto employment;
    private String accountNumber;
    private boolean isInsuranceEnabled;
    private boolean isSalaryClient;

}
