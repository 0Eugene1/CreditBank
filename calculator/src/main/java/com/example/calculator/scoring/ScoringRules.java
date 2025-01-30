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
    private BigDecimal baseRate;

    public static BigDecimal applyEmploymentStatus(EmploymentStatusEnum employmentStatus, BigDecimal baseRate) {
        log.debug("Applying employment status rule.Employment status: {}", employmentStatus);

        return switch (employmentStatus) {
            case UNEMPLOYED -> throw new IllegalArgumentException("Отказ по причине: безработный.");
            case SELF_EMPLOYED -> baseRate.add(BigDecimal.valueOf(2));
            case BUSINESS_OWNER -> baseRate.add(BigDecimal.valueOf(1));
        };
    }


    public static BigDecimal applyPositionStatus(PositionEnum position, BigDecimal baseRate) {
        log.debug("Applying position status rule. Position: {}", position);

        return switch (position) {
            case MIDDLE_MANAGER -> baseRate.subtract(BigDecimal.valueOf(2));
            case TOP_MANAGER -> baseRate.subtract(BigDecimal.valueOf(3));
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


    public static BigDecimal applyMaritalStatus(MaritalStatusEnum maritalStatus, BigDecimal baseRate) {
        log.debug("Applying marital status rule. Marital status: {}", maritalStatus);

        return switch (maritalStatus) {
            case MARRIED -> baseRate.subtract(BigDecimal.valueOf(3));
            case DIVORCED -> baseRate.add(BigDecimal.valueOf(1));
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


    public static BigDecimal applyGenderRule(GenderEnum gender, LocalDate birthDate, BigDecimal baseRate) {

        if (birthDate == null) {
            log.error("Birthdate cannot be null");
            throw new IllegalArgumentException("Birthdate cannot be null");
        }

        int age = Period.between(birthDate, LocalDate.now()).getYears();
        log.debug("Applying gender rule. Gender: {}, Age: {}", gender, age);

        return switch (gender) {
            case FEMALE -> (age >= 32 && age <= 60) ? baseRate.subtract(BigDecimal.valueOf(3)) : baseRate;
            case MALE -> (age >= 30 && age <= 55) ? baseRate.subtract(BigDecimal.valueOf(3)) : baseRate;
            case NON_BINARY -> baseRate.add(BigDecimal.valueOf(7));
        };
    }

    public static boolean isExperienceValid(Integer workExperienceTotal, Integer workExperienceCurrent) {
        return workExperienceTotal >= 18 && workExperienceCurrent >= 3;
    }


}