package com.example.calculator.prescoring;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertThrows;

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


    @ParameterizedTest
    @ValueSource(strings = {"25000", "100000"})
    public void testValidateCreditAmount_ValidAmounts_ShouldNotThrowException(String amount) {
        try {
            PrescoringRules.validateCreditAmount(new BigDecimal(amount));
        } catch (IllegalArgumentException e) {
            fail("Exception should not be thrown for valid amount: " + amount);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"5000", "9999", "0"})
    public void testValidateCreditAmount_InvalidAmounts_ShouldThrowException(String amount) {
        assertThrows(IllegalArgumentException.class, () -> {
            PrescoringRules.validateCreditAmount(new BigDecimal(amount));
        });
    }


    @ParameterizedTest
    @ValueSource(ints = {6, 12, 24, 36})
    public void testValidateLoanTerm_ValidTerms_ShouldNotThrowException(int term) {
        try {
            PrescoringRules.validateLoanTerm(term);
        } catch (IllegalArgumentException e) {
            fail("Exception should not be thrown for valid loan term: " + term);
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 5})
    public void testValidateLoanTerm_InvalidTerms_ShouldThrowException(int term) {
        assertThrows(IllegalArgumentException.class, () -> PrescoringRules.validateLoanTerm(term));
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


    @ParameterizedTest
    @ValueSource(strings = {"user@example.com", "test.user@domain.org", "name.surname@mail.co"})
    public void testValidateEmail_ValidEmails_ShouldNotThrowException(String email) {
        try {
            PrescoringRules.validateEmail(email);
        } catch (IllegalArgumentException e) {
            fail("Exception should not be thrown for valid email: " + email);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"test.com"})
    public void testValidateEmail_InvalidEmails_ShouldThrowException(String email) {
        assertThrows(IllegalArgumentException.class, () -> PrescoringRules.validateEmail(email));
    }


    @ParameterizedTest
    @ValueSource(strings = {"1234", "5678", "4321"})
    public void testValidatePassport_ValidSeries_ShouldNotThrowException(String series) {
        try {
            PrescoringRules.validatePassport(series, "123456");
        } catch (IllegalArgumentException e) {
            fail("Exception should not be thrown for valid passport series: " + series);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"12", "123", "abcd"})
    public void testValidatePassport_InvalidSeries_ShouldThrowException(String series) {
        assertThrows(IllegalArgumentException.class, () -> PrescoringRules.validatePassport(series, "123456"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"123456", "654321", "000001"})
    public void testValidatePassport_ValidNumbers_ShouldNotThrowException(String number) {
        try {
            PrescoringRules.validatePassport("1234", number);
        } catch (IllegalArgumentException e) {
            fail("Exception should not be thrown for valid passport number: " + number);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"12", "123", "000"})
    public void testValidatePassport_InvalidNumbers_ShouldThrowException(String number) {
        assertThrows(IllegalArgumentException.class, () -> PrescoringRules.validatePassport("1234", number));
    }

}
