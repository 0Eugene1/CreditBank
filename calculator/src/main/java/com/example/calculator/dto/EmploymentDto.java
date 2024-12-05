package com.example.calculator.dto;

import com.example.calculator.enums.EmploymentStatusEnum;
import com.example.calculator.enums.PositionEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class EmploymentDto {

    private EmploymentStatusEnum employmentStatus;
    private String employerINN;
    private BigDecimal salary;
    private PositionEnum position;
    @NotNull(message = "Общий стаж работы не может быть пустым")
    private Integer workExperienceTotal;
    @NotNull(message = "Текущий стаж работы не может быть пустым")
    private Integer workExperienceCurrent;

}
