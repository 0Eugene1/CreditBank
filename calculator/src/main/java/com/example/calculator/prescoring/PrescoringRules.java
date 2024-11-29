package com.example.calculator.prescoring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.regex.Pattern;

public class PrescoringRules {

    private static final Logger logger = LoggerFactory.getLogger(PrescoringRules.class);

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[a-z0-9A-Z_!#$%&'*+/=?`{|}~^.-]+@[a-z0-9A-Z.-]+$");

    public static boolean validateName(String name) {
        if (name == null || !name.matches("^[A-Za-z]{2,30}$")) {
            logger.warn("Invalid name: {}", name);
            throw new IllegalArgumentException("Неверно указано имя: " + name);
        }
        return true;
    }

    public static boolean validateCreditAmount(BigDecimal amount) { // Сумма кредита
        logger.debug("Validating credit amount: {}", amount);
        BigDecimal minAmount = new BigDecimal("20000"); // -1 if amount < than minAmount,0 if equals, 1 if amount > minAmount

        if (amount.compareTo(minAmount) < 0) {
            logger.warn("Invalid credit amount: {}. It must be greater than or equal to {}", amount, minAmount);
            throw new IllegalArgumentException("Сумма кредита не одобрена: " + amount);
        }
        return true;
    }

    public static boolean validateLoanTerm(int loanTerm) {  // Срок кредита
        logger.debug("Validating loan term: {}", loanTerm);

        if (loanTerm < 6) {
            logger.warn("Invalid loan term: {}. It must be at least 6 months.", loanTerm);
            throw new IllegalArgumentException("Срок кредита должен быть минимум 6 месяцв: " + loanTerm);
        }
        return true;
    }

    public static boolean validateBirthDate(LocalDate birthDate) {
        logger.debug("Validating birth date: {}", birthDate);
        if (birthDate == null || birthDate.isAfter(LocalDate.now().minusYears(18))) {
            logger.warn("Invalid birth date: {}. Age must be at least 18 years.", birthDate);
            throw new IllegalArgumentException("Минимальный возраст для получения кредита - 18 лет: " + birthDate);
        }
        return true;
    }

    public static boolean validateEmail(String email) {
        logger.debug("Validating email: {}", email);
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            logger.warn("Invalid email: {}", email);
            throw new IllegalArgumentException("Неверный email: " + email);
        }
        return true;
    }

    public static boolean validatePassport(String passportSeries, String passportNumber) {
        if (passportSeries == null || passportSeries.length() != 4 || !passportSeries.matches("\\d{4}")) {
            logger.warn("Invalid passport series: {}", passportSeries);
            throw new IllegalArgumentException("Серия паспорта должна быть 4 символа: " + passportSeries);
        }
        if (passportNumber == null || passportNumber.length() != 6 || !passportNumber.matches("\\d{6}")) {
            logger.warn("Invalid passport number: {}", passportNumber);
            throw new IllegalArgumentException("Номер паспорта должен быть 6 символов: " + passportNumber);
        }
        return true;
    }
}