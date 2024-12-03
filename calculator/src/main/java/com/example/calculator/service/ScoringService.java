package com.example.calculator.service;

import com.example.calculator.dto.ScoringDataDto;
import com.example.calculator.scoring.ScoringRules;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.example.calculator.scoring.ScoringRules.*;

@Slf4j
@Service
public class ScoringService {


    @Value("${loan.base-rate}")
    private double baseRate;

    @Operation(summary = "Calculate the loan rate based on scoring data",
            description = "Calculates the modified loan rate based on various conditions like age, experience, loan amount, etc.")

    public double calculateRate(ScoringDataDto data) {
        log.info("Start calculating rate for ScoringData: {}", data);

        // Проверка стажа
        ScoringRules.isExperienceValid(data.getEmployment().getWorkExperienceTotal(), data.getEmployment().getWorkExperienceCurrent());

        double modifiedRate = baseRate;  // Используем локальную переменную для расчётов

        if (modifiedRate < 0) {
            modifiedRate = 10; // Устанавливаем минимальную ставку
        }
        //modified rate
        log.info("Base rate: {}", modifiedRate);

        modifiedRate = applyEmploymentStatus(data.getEmployment().getEmploymentStatus(), modifiedRate);
        log.info("Rate after applying employment status: {}", modifiedRate);

        modifiedRate = applyPositionStatus(data.getEmployment().getPosition(), modifiedRate);
        log.info("Rate after applying position status: {}", modifiedRate);

        modifiedRate = applyMaritalStatus(data, modifiedRate);
        log.info("Rate after applying marital status: {}", modifiedRate);

        modifiedRate = applyGenderRule(data, modifiedRate);
        log.info("Rate after applying gender rule: {}", modifiedRate);

        log.info("Final modified rate: {}", modifiedRate);
        return modifiedRate;
    }
}