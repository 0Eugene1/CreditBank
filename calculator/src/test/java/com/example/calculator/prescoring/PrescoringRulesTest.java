package com.example.calculator.prescoring;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PrescoringRulesTest {

    @Test
    public void testValidateName_ValidName_ShouldReturnTrue() {
        String name = "John";
        boolean result = PrescoringRules.validateName(name);
        assertTrue(result);
    }

    @Test
    public void testValidateName_InvalidName_ShouldThrowException() {
        String name = "J"; // Недостаточная длина
        assertThrows(IllegalArgumentException.class, () -> {
            PrescoringRules.validateName(name);
        });
    }

    @Test
    public void testValidateName_NameWithDigits_ShouldThrowException() {
        String name = "John123"; // Имя с цифрами
        assertThrows(IllegalArgumentException.class, () -> {
            PrescoringRules.validateName(name);
        });
    }

    @Test
    public void testValidateCreditAmount_ValidAmount_ShouldReturnTrue() {
        BigDecimal amount = new BigDecimal("25000");
        boolean result = PrescoringRules.validateCreditAmount(amount);
        assertTrue(result);
    }

    @Test
    public void testValidateCreditAmount_InvalidAmount_ShouldThrowException() {
        BigDecimal amount = new BigDecimal("10000"); // Сумма меньше минимальной
        assertThrows(IllegalArgumentException.class, () -> {
            PrescoringRules.validateCreditAmount(amount);
        });
    }

    @Test
    public void testValidateLoanTerm_ValidTerm_ShouldReturnTrue() {
        int loanTerm = 12; // Срок кредита больше 6 месяцев
        boolean result = PrescoringRules.validateLoanTerm(loanTerm);
        assertTrue(result);
    }

    @Test
    public void testValidateLoanTerm_InvalidTerm_ShouldThrowException() {
        int loanTerm = 5; // Срок меньше 6 месяцев
        assertThrows(IllegalArgumentException.class, () -> {
            PrescoringRules.validateLoanTerm(loanTerm);
        });
    }

    @Test
    public void testValidateBirthDate_ValidBirthDate_ShouldReturnTrue() {
        LocalDate birthDate = LocalDate.of(2000, 1, 1); // Возраст 24 года
        boolean result = PrescoringRules.validateBirthDate(birthDate);
        assertTrue(result);
    }

    @Test
    public void testValidateBirthDate_Underage_ShouldThrowException() {
        LocalDate birthDate = LocalDate.of(2010, 1, 1); // Возраст 14 лет
        assertThrows(IllegalArgumentException.class, () -> {
            PrescoringRules.validateBirthDate(birthDate);
        });
    }

    @Test
    public void testValidateEmail_ValidEmail_ShouldReturnTrue() {
        String email = "test@example.com";
        boolean result = PrescoringRules.validateEmail(email);
        assertTrue(result);
    }

    @Test
    public void testValidateEmail_InvalidEmail_ShouldThrowException() {
        String email = "test.com"; // Невалидный email
        assertThrows(IllegalArgumentException.class, () -> {
            PrescoringRules.validateEmail(email);
        });
    }

    @Test
    public void testValidatePassport_ValidPassport_ShouldReturnTrue() {
        String passportSeries = "1234";
        String passportNumber = "123456";
        boolean result = PrescoringRules.validatePassport(passportSeries, passportNumber);
        assertTrue(result);
    }

    @Test
    public void testValidatePassport_InvalidPassportSeries_ShouldThrowException() {
        String passportSeries = "12"; // Неверная длина серии паспорта
        String passportNumber = "123456";
        assertThrows(IllegalArgumentException.class, () -> {
            PrescoringRules.validatePassport(passportSeries, passportNumber);
        });
    }

    @Test
    public void testValidatePassport_InvalidPassportNumber_ShouldThrowException() {
        String passportSeries = "1234";
        String passportNumber = "123"; // Неверная длина номера паспорта
        assertThrows(IllegalArgumentException.class, () -> {
            PrescoringRules.validatePassport(passportSeries, passportNumber);
        });
    }
}
