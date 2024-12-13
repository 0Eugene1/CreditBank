package com.example.deal.dto;

import com.example.deal.enums.Gender;
import com.example.deal.enums.MaritalStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)  // Игнорирует неизвестные поля
public class FinishRegistrationRequestDto {

    @NotNull(message = "Гендер не должен быть пуст")
    private Gender gender;

    @NotNull(message = "Семейное положение не должно быть пустым")
    private MaritalStatus maritalStatus;

    @NotNull(message = "dependentAmount не должно быть нулевым")
    @PositiveOrZero(message = "dependentAmount не может быть отрицательным")
    private Integer dependentAmount;

    @NotNull(message = "Дата выдачи паспорта не должна быть пустой")
    private LocalDate passportIssueDate;

    @NotNull(message = "Код подразделения паспорта не должен быть пустым")
    @Pattern(regexp = "\\d{6}", message = "Код подразделения паспорта должен содержать 6 цифр")
    private String passportIssueBranch;

    @NotNull(message = "Employment не должен быть пуст")
    private EmploymentDto employment;

    @NotEmpty(message = "Номер аккаунта не должен быть пустым")
    @Pattern(regexp = "\\d{20}", message = "Номер счета должен содержать 20 цифр")
    private String accountNumber;
}

