package com.example.calculator.scoring;

import com.example.calculator.dto.EmploymentDto;
import com.example.calculator.dto.ScoringDataDto;
import com.example.calculator.enums.EmploymentStatusEnum;
import com.example.calculator.enums.PositionEnum;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

@Slf4j
public class ScoringRules {

    @Value("${loan.base-rate}")
    private double baseRate;

    public static double applyEmploymentStatus(EmploymentStatusEnum employmentStatus, double baseRate) {

        log.debug("Applying employment status rule.Employment status: {}", employmentStatus);

        return switch (employmentStatus) {
            case UNEMPLOYED -> {
                log.warn("Rejected due to employment status: UNEMPLOYED");
                throw new IllegalArgumentException("Отказ по причине: безработный.");
            }
            case SELF_EMPLOYED -> {
                log.info("Self-employed: increasing rate by 2");
                yield baseRate + 2;
            }
            case BUSINESS_OWNER -> {
                log.info("Business owner: increasing rate by 1");
                yield baseRate + 1;
            }
            default -> baseRate;
        };
    }


    public static double applyPositionStatus(PositionEnum position, double baseRate) {
        log.debug("Applying position status rule. Position: {}", position);

        return switch (position) {
            case MIDDLE_MANAGER -> {
                log.info("Middle manager: decreasing rate by 2");
                yield baseRate - 2;
            }
            case TOP_MANAGER -> {
                log.info("Top manager: decreasing rate by 3");
                yield baseRate - 3;
            }
            default -> baseRate;
        };
    }


    public static void isLoanAmountAcceptable(ScoringDataDto data) {
        BigDecimal loanAmount = data.getAmount();
        BigDecimal maxLoanAmount = data.getEmployment().getSalary().multiply(BigDecimal.valueOf(24));

        log.debug("Checking loan amount. Loan amount: {}, Max loan amount: {}", loanAmount, maxLoanAmount);
        if (loanAmount.compareTo(maxLoanAmount) > 0) {
            throw new IllegalArgumentException("Отказ по сумме займа."); // true, если допустимо
        }
    }


    public static double applyMaritalStatus(ScoringDataDto data, double baseRate) {
        log.debug("Applying marital status rule. Marital status: {}", data.getMaritalStatus());

        return switch (data.getMaritalStatus()) {
            case MARRIED -> {
                log.info("Married: decreasing rate by 3");
                yield baseRate - 3;
            }
            case DIVORCED -> {
                log.info("Divorced: increasing rate by 1");
                yield baseRate + 1;
            }
            default -> baseRate;
        };
    }


    public static boolean isAgeValid(ScoringDataDto data) {
        int age = Period.between(data.getBirthDate(), LocalDate.now()).getYears();
        log.debug("Checking age: {}", age);

        if (age >= 20 && age <= 65) {
            return true;
        } else {
            throw new IllegalArgumentException("Отказ по возрасту"); // true, если возраст в пределах
        }
    }



    public static double applyGenderRule(ScoringDataDto data, double baseRate) {
        int age = Period.between(data.getBirthDate(), LocalDate.now()).getYears();
        log.debug("Applying gender rule. Gender: {}, Age: {}", data.getGender(), age);

        return switch (data.getGender()) {
            case FEMALE -> {
                if (age >= 32 && age <= 60) {
                    log.info("Female: age between 32 and 60, decreasing rate by 3");
                    yield baseRate - 3;
                }
                yield baseRate;
            }
            case MALE -> {
                if (age >= 30 && age <= 55) {
                    log.info("Male: age between 30 and 55, decreasing rate by 3");
                    yield baseRate - 3;
                }
                yield baseRate;
            }
            case NON_BINARY -> {
                log.info("Non-binary: increasing rate by 7");
                yield baseRate + 7;
            }
            default -> baseRate;
        };
    }

    public static boolean isExperienceValid(Integer workExperienceTotal, Integer workExperienceCurrent) {
        if (workExperienceTotal == null || workExperienceCurrent == null) {
            throw new IllegalArgumentException("Отказ по стажу: стаж не может быть пустым.");
        }

        // Проверка по условиям
        if (workExperienceTotal >= 18 && workExperienceCurrent >= 3) {
            return true;
        } else {
            throw new IllegalArgumentException("Отказ по стажу: недостаточный стаж.");
        }
    }
}