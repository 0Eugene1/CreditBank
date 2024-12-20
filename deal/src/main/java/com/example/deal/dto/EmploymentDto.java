package com.example.deal.dto;

import com.example.deal.enums.EmploymentPosition;
import com.example.deal.enums.EmploymentStatus;
import jakarta.validation.constraints.*;
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

    @NotNull(message = "Необходимо указать ИНН работодателя")
    @Pattern(regexp = "\\d{10}|\\d{12}", message = "ИНН должен содержать 10 или 12 цифр")
    private String employerInn;

    @NotNull(message = "Зарплата не должна быть нулевой")
    private BigDecimal salary;

    @NotNull(message = "Должность не должна быть нулевой")
    private EmploymentPosition position;

    @NotNull(message = "Общий стаж работы не должен быть нулевым")
    @PositiveOrZero(message = "Общий стаж работы не может быть отрицательным")
    private Integer workExperienceTotal;

    @NotNull(message = "Текущий опыт работы не должен быть нулевым")
    @PositiveOrZero(message = "Текущий опыт работы не может быть отрицательным")
    private Integer workExperienceCurrent;

}
