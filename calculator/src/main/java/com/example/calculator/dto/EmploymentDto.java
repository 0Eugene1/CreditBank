package com.example.calculator.dto;

import com.example.calculator.enums.EmploymentStatusEnum;
import com.example.calculator.enums.PositionEnum;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class EmploymentDto {

    private EmploymentStatusEnum employmentStatus;
    private String employerINN;
    private BigDecimal salary;
    private PositionEnum position;
    private Integer workExperienceTotal;
    private Integer workExperienceCurrent;

}
