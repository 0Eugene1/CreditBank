package com.example.calculator.prescoring;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PrescoringRulesTest {

    // Параметризированный тест для валидных имен
    @ParameterizedTest
    @ValueSource(strings = {"John", "Jane", "Michael", "Alex"})
    public void testValidateName_ValidName_ShouldReturnTrue(String name) {
        try {
            PrescoringRules.validateName(name);
        } catch (IllegalArgumentException e) {
            fail("Exception should not be thrown for valid name: " + name);
        }
    }

    // Параметризированный тест для невалидных имен
    @ParameterizedTest
    @ValueSource(strings = {"", "J", "123", "@john", "JohnDoe123", "Jhon213"})
    public void testValidateName_InvalidName_ShouldThrowException(String name) {
        assertThrows(IllegalArgumentException.class, () -> PrescoringRules.validateName(name));
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
        try {
            PrescoringRules.validateCreditAmount(amount);
        } catch (IllegalArgumentException e) {
            fail("Exception should not be thrown for valid amount");
        }
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
        try {
            PrescoringRules.validateLoanTerm(loanTerm);
        } catch (IllegalArgumentException e) {
            fail("Exception should not be thrown for valid loan term");
        }
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
        try {
            PrescoringRules.validateBirthDate(birthDate);
        } catch (IllegalArgumentException e) {
            fail("Exception should not be thrown for valid birth date");
        }
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
        try {
            PrescoringRules.validateEmail(email);
        } catch (IllegalArgumentException e) {
            fail("Exception should not be thrown for valid email");
        }
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
        try {
            PrescoringRules.validatePassport(passportSeries, passportNumber);
        } catch (IllegalArgumentException e) {
            fail("Exception should not be thrown for valid passport");
        }
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
