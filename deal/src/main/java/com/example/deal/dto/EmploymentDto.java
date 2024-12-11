package com.example.deal.dto;

import com.example.deal.enums.EmploymentPosition;
import com.example.deal.enums.EmploymentStatus;
import com.example.deal.enums.Gender;
import com.example.deal.enums.MaritalStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmploymentDto {

    @NotNull(message = "Статус занятости не должен быть нулевым")
    private EmploymentStatus employmentStatus;
    @NotBlank(message = "Необходимо указать ИНН работодателя")
    private String employerINN;
    @NotNull(message = "Зарплата не должна быть нулевой")
    private BigDecimal salary;
    @NotNull(message = "Должность не должна быть нулевой")
    private EmploymentPosition position;
    @NotNull(message = "Общий стаж работы не должен быть нулевым")
    private Integer workExperienceTotal;
    @NotNull(message = "Текущий опыт работы не должен быть нулевым")
    private Integer workExperienceCurrent;
}
