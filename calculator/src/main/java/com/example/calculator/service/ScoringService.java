package com.example.calculator.service;

import com.example.calculator.dto.ScoringDataDto;
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

    public double calculateRate(@Parameter(description = "Scoring data for calculating the loan rate") ScoringDataDto data) {
        log.info("Start calculating rate for ScoringData: {}", data);

        // Проверки на отказ
        if (!isLoanAmountAcceptable(data)) {
            log.error("Loan amount validation failed for ScoringData: {}", data);
            throw new IllegalArgumentException("Отказ по сумме займа");
        }
        // Проверка стажа
        if (!isExperienceValid(data)) {
            log.error("Experience validation failed for Scoring data: {}", data);
            throw new IllegalArgumentException("Отказ по стажу");
        }
        if (!isAgeValid(data)) {
            log.error("Age validation failed for Scoring data: {}", data);
            throw new IllegalArgumentException("Отказ по возрасту");
        }

        double modifiedRate = baseRate;  // Используем локальную переменную для расчётов

        if (modifiedRate < 0) {
            modifiedRate = 10; // Устанавливаем минимальную ставку
        }
        //modified rate
        log.info("Base rate: {}", modifiedRate);

        modifiedRate = applyEmploymentStatus(data, modifiedRate);
        log.info("Rate after applying employment status: {}", modifiedRate);

        modifiedRate = applyPositionStatus(data, modifiedRate);
        log.info("Rate after applying position status: {}", modifiedRate);

        modifiedRate = applyMaritalStatus(data, modifiedRate);
        log.info("Rate after applying marital status: {}", modifiedRate);

        modifiedRate = applyGenderRule(data, modifiedRate);
        log.info("Rate after applying gender rule: {}", modifiedRate);

        log.info("Final modified rate: {}", modifiedRate);
        return modifiedRate;
    }
}