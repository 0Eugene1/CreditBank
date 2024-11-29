package com.example.calculator.scoring;

import com.example.calculator.dto.EmploymentDto;
import com.example.calculator.dto.ScoringDataDto;
import com.example.calculator.enums.EmploymentStatusEnum;
import com.example.calculator.enums.PositionEnum;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

public class ScoringRules {

    private static final Logger logger = LoggerFactory.getLogger(ScoringRules.class);

    @Value("${loan.base-rate}") //FIXME НЕ БЫЛО
    private double baseRate;

    public static double applyEmploymentStatus(ScoringDataDto data, double baseRate) { //FIXME БЫЛО currentRate
        EmploymentStatusEnum status = data.getEmployment().getEmploymentStatus();
        logger.debug("Applying employment status rule.Employment status: {}", status);

        return switch (status) {
            case UNEMPLOYED -> {
                logger.warn("Rejected due to employment status: UNEMPLOYED");
                throw new IllegalArgumentException("Отказ по причине: безработный.");
            }
            case SELF_EMPLOYED -> {
                logger.info("Self-employed: increasing rate by 2");
                yield baseRate + 2;
            }
            case BUSINESS_OWNER -> {
                logger.info("Business owner: increasing rate by 1");
                yield baseRate + 1;
            }
            default -> baseRate;
        };
    }


    public static double applyPositionStatus(ScoringDataDto data, double baseRate) { //FIXME БЫЛО currentRate
        PositionEnum position = data.getEmployment().getPosition();
        logger.debug("Applying position status rule. Position: {}", position);

        return switch (position) {
            case MIDDLE_MANAGER -> {
                logger.info("Middle manager: decreasing rate by 2");
                yield baseRate - 2;
            }
            case TOP_MANAGER -> {
                logger.info("Top manager: decreasing rate by 3");
                yield baseRate - 3;
            }
            default -> baseRate;
        };
    }


    public static boolean isLoanAmountAcceptable(ScoringDataDto data) {
        BigDecimal loanAmount = data.getAmount();
        BigDecimal maxLoanAmount = data.getEmployment().getSalary().multiply(BigDecimal.valueOf(24));

        logger.debug("Checking loan amount. Loan amount: {}, Max loan amount: {}", loanAmount, maxLoanAmount);
        return loanAmount.compareTo(maxLoanAmount) <= 0; // true, если допустимо
    }


    public static double applyMaritalStatus(ScoringDataDto data, double baseRate) {  //FIXME БЫЛО currentRate
        logger.debug("Applying marital status rule. Marital status: {}", data.getMaritalStatus());

        return switch (data.getMaritalStatus()) {
            case MARRIED -> {
                logger.info("Married: decreasing rate by 3");
                yield baseRate - 3;
            }
            case DIVORCED -> {
                logger.info("Divorced: increasing rate by 1");
                yield baseRate + 1;
            }
            default -> baseRate;
        };
    }


    public static boolean isAgeValid(ScoringDataDto data) {
        int age = Period.between(data.getBirthDate(), LocalDate.now()).getYears();
        logger.debug("Checking age: {}", age);

        return age >= 20 && age <= 65; // true, если возраст в пределах
    }

    public static double applyGenderRule(ScoringDataDto data, double baseRate) {  //FIXME БЫЛО currentRate
        int age = Period.between(data.getBirthDate(), LocalDate.now()).getYears();
        logger.debug("Applying gender rule. Gender: {}, Age: {}", data.getGender(), age);

        return switch (data.getGender()) {
            case FEMALE -> {
                if (age >= 32 && age <= 60) {
                    logger.info("Female: age between 32 and 60, decreasing rate by 3");
                    yield baseRate - 3;
                }
                yield baseRate;
            }
            case MALE -> {
                if (age >= 30 && age <= 55) {
                    logger.info("Male: age between 30 and 55, decreasing rate by 3");
                    yield baseRate - 3;
                }
                yield baseRate;
            }
            case NON_BINARY -> {
                logger.info("Non-binary: increasing rate by 7");
                yield baseRate + 7;
            }
            default -> baseRate;
        };
    }
    public static boolean isExperienceValid(ScoringDataDto data) {
        EmploymentDto employment = data.getEmployment();
        logger.debug("Checking experience. Total experience: {}, Current experience: {}",
                employment.getWorkExperienceTotal(), employment.getWorkExperienceCurrent());


        return employment.getWorkExperienceTotal() >= 18 &&
                employment.getWorkExperienceCurrent() >= 3; // true, если стаж валиден
    }
}