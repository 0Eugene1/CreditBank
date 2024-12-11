package com.example.deal.dto;

import com.example.deal.enums.Gender;
import com.example.deal.enums.MaritalStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinishRegistrationRequestDto {

    @NotNull(message = "Гендер не должен быть пуст")
    private Gender gender;
    @NotNull(message = "Семейное положение не должно быть пустым")
    private MaritalStatus maritalStatus;
    @NotNull(message = "Сумма не должна быть нулем")
    private Integer dependentAmount;
    @NotNull(message = "Выдача паспорта не должна быть пустой")
    private LocalDate passportIssueDate;
    @NotNull(message = "Отделение выдачи паспорта не должно быть пустым")
    private String passportIssueBrach;
    @NotNull(message = "Employment не должен быть пуст")
    private EmploymentDto employment;
    @NotEmpty(message = "Номер аккаунта не должен быть пуст")
    private String accountNumber;
}
