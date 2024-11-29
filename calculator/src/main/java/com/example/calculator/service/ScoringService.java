package com.example.calculator.service;
import com.example.calculator.dto.ScoringDataDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.example.calculator.scoring.ScoringRules.*;


@Service
public class ScoringService {

    private static final Logger logger = LoggerFactory.getLogger(ScoringService.class);

    @Value("${loan.base-rate}")
    private double baseRate;

    @Operation(summary = "Calculate the loan rate based on scoring data",
            description = "Calculates the modified loan rate based on various conditions like age, experience, loan amount, etc.")

    public double  calculateRate(@Parameter(description = "Scoring data for calculating the loan rate")ScoringDataDto data) {
        logger.info("Start calculating rate for ScoringData: {}", data);

        // Проверки на отказ
        if (!isLoanAmountAcceptable(data)) {
            logger.error("Loan amount validation failed for ScoringData: {}", data);
            throw new IllegalArgumentException("Отказ по сумме займа");
        }
        // Проверка стажа
        if (!isExperienceValid(data)) {
            logger.error("Experience validation failed for Scoring data: {}", data);
            throw new IllegalArgumentException("Отказ по стажу");
        }
        if (!isAgeValid(data)) {
            logger.error("Age validation failed for Scoring data: {}", data);
            throw new IllegalArgumentException("Отказ по возрасту");
        }

        double modifiedRate = baseRate;  // Используем локальную переменную для расчётов

        if (modifiedRate < 0) {
            modifiedRate = 10; // Устанавливаем минимальную ставку // FIXME НЕ БЫЛО
        }
        //modified rate
        logger.info("Base rate: {}", modifiedRate);

        modifiedRate = applyEmploymentStatus(data, modifiedRate);
        logger.info("Rate after applying employment status: {}", modifiedRate);

        modifiedRate = applyPositionStatus(data, modifiedRate);
        logger.info("Rate after applying position status: {}", modifiedRate);

        modifiedRate = applyMaritalStatus(data, modifiedRate);
        logger.info("Rate after applying marital status: {}", modifiedRate);

        modifiedRate = applyGenderRule(data, modifiedRate);
        logger.info("Rate after applying gender rule: {}", modifiedRate);

        logger.info("Final modified rate: {}", modifiedRate);
        return modifiedRate;
    }
}