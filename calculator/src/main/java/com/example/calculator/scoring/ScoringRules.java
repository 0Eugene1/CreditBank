package com.example.calculator.scoring;

import com.example.calculator.dto.ScoringDataDto;
import com.example.calculator.enums.EmploymentStatusEnum;
import com.example.calculator.enums.GenderEnum;
import com.example.calculator.enums.MaritalStatusEnum;
import com.example.calculator.enums.PositionEnum;
import lombok.extern.slf4j.Slf4j;
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
                throw new IllegalArgumentException("Отказ по причине: безработный.");
            }
            case SELF_EMPLOYED -> {
                yield baseRate + 2;
            }
            case BUSINESS_OWNER -> {
                yield baseRate + 1;
            }
            default -> baseRate;
        };
    }


    public static double applyPositionStatus(PositionEnum position, double baseRate) {
        log.debug("Applying position status rule. Position: {}", position);

        return switch (position) {
            case MIDDLE_MANAGER -> {
                yield baseRate - 2;
            }
            case TOP_MANAGER -> {
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


    public static double applyMaritalStatus(MaritalStatusEnum maritalStatus, double baseRate) {
        log.debug("Applying marital status rule. Marital status: {}", maritalStatus);

        return switch (maritalStatus) {
            case MARRIED -> {
                yield baseRate - 3;
            }
            case DIVORCED -> {
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
            throw new IllegalArgumentException("Отказ по возрасту");
        }
    }


    public static double applyGenderRule(GenderEnum gender, LocalDate birthDate, double baseRate) {
        int age = Period.between(birthDate, LocalDate.now()).getYears();
        log.debug("Applying gender rule. Gender: {}, Age: {}", gender, age);

        return switch (gender) {
            case FEMALE -> {
                if (age >= 32 && age <= 60) {
                    yield baseRate - 3;
                }
                yield baseRate;
            }
            case MALE -> {
                if (age >= 30 && age <= 55) {
                    yield baseRate - 3;
                }
                yield baseRate;
            }
            case NON_BINARY -> {
                yield baseRate + 7;
            }
            default -> baseRate;
        };
    }

    public static boolean isExperienceValid(Integer workExperienceTotal, Integer workExperienceCurrent) {

        // Проверка по условиям
        if (workExperienceTotal >= 18 && workExperienceCurrent >= 3) {
            return true;
        } else {
            throw new IllegalArgumentException("Отказ по стажу: недостаточный стаж.");
        }
    }
}