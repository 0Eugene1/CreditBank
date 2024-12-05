package com.example.calculator.service;

import com.example.calculator.dto.ScoringDataDto;
import com.example.calculator.scoring.ScoringRules;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.example.calculator.scoring.ScoringRules.*;

@Slf4j
@Service
public class ScoringService {


    @Value("${loan.base-rate:10.0}")
    private double baseRate;

    @Operation(summary = "Calculate the loan rate based on scoring data",
            description = "Calculates the modified loan rate based on various conditions like age, experience, loan amount, etc.")

    public double calculateRate(ScoringDataDto data) {
        log.info("Start calculating rate for ScoringData: {}", data);

        // Проверка стажа
        ScoringRules.isExperienceValid(data.getEmployment().getWorkExperienceTotal(), data.getEmployment().getWorkExperienceCurrent());

        double modifiedRate = baseRate;  // Используем локальную переменную для расчётов

        if (modifiedRate < 0) {
            modifiedRate = 10.0; // Устанавливаем минимальную ставку
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