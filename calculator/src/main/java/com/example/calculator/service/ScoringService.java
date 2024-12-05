package com.example.calculator.service;

import com.example.calculator.dto.ScoringDataDto;
import com.example.calculator.scoring.ScoringRules;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static com.example.calculator.scoring.ScoringRules.*;

@Slf4j
@Service
public class ScoringService {


    @Value("${loan.base-rate:10.0}")
    private BigDecimal baseRate;

    @Operation(summary = "Calculate the loan rate based on scoring data",
            description = "Calculates the modified loan rate based on various conditions like age, experience, loan amount, etc.")

    public BigDecimal calculateRate(ScoringDataDto data) {
        log.info("Start calculating rate for ScoringData: {}", data);

        // Проверка стажа
        ScoringRules.isExperienceValid(data.getEmployment().getWorkExperienceTotal(), data.getEmployment().getWorkExperienceCurrent());

        BigDecimal modifiedRate = baseRate;  // Используем локальную переменную для расчётов

        if (modifiedRate.compareTo(BigDecimal.valueOf(10.0)) < 0) {
            modifiedRate = BigDecimal.valueOf(10.0);
        }
        //modified rate

        modifiedRate = applyEmploymentStatus(data.getEmployment().getEmploymentStatus(), modifiedRate);

        modifiedRate = applyPositionStatus(data.getEmployment().getPosition(), modifiedRate);

        modifiedRate = applyMaritalStatus(data.getMaritalStatus(), modifiedRate);

        modifiedRate = applyGenderRule(data.getGender(), data.getBirthDate(), modifiedRate);

        log.info("Final modified rate: {}", modifiedRate);
        return modifiedRate;
    }
}