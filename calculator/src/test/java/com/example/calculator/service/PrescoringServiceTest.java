package com.example.calculator.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.calculator.dto.LoanStatementRequestDto;
import com.example.calculator.dto.ScoringDataDto;
import com.example.calculator.prescoring.PrescoringRules;
import com.example.calculator.service.PrescoringService;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
public class PrescoringServiceTest {

    @InjectMocks
    private PrescoringService prescoringService;

    @Mock
    private PrescoringRules prescoringRulesMock;

    private LoanStatementRequestDto validRequest;
    private LoanStatementRequestDto invalidRequest;

    private ScoringDataDto validScoringData;
    private ScoringDataDto invalidScoringData;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Создание валидных объектов с использованием сеттеров
        validRequest = new LoanStatementRequestDto();
        validRequest.setFirstName("John");
        validRequest.setLastName("Doe");
        validRequest.setMiddleName("Middle");
        validRequest.setAmount(BigDecimal.valueOf(5000));
        validRequest.setTerm(12);
        validRequest.setBirthDate(LocalDate.of(1990, 1, 1));
        validRequest.setEmail("john.doe@example.com");
        validRequest.setPassportSeries("123456");
        validRequest.setPassportNumber("12345");

        invalidRequest = new LoanStatementRequestDto();
        invalidRequest.setFirstName("John");
        invalidRequest.setLastName("Doe");
        invalidRequest.setMiddleName("Middle");
        invalidRequest.setAmount(BigDecimal.valueOf(-5000));  // Неверная сумма
        invalidRequest.setTerm(12);
        invalidRequest.setBirthDate(LocalDate.of(1990, 1, 1));
        invalidRequest.setEmail("invalid_email");  // Неверный email
        invalidRequest.setPassportSeries("123456");
        invalidRequest.setPassportNumber("12345");

        validScoringData = new ScoringDataDto();
        validScoringData.setFirstName("John");
        validScoringData.setLastName("Doe");
        validScoringData.setAmount(BigDecimal.valueOf(5000));
        validScoringData.setTerm(12);
        validScoringData.setBirthDate(LocalDate.of(1990, 1, 1));
        validScoringData.setPassportSeries("123456");
        validScoringData.setPassportNumber("12345");

        invalidScoringData = new ScoringDataDto();
        invalidScoringData.setFirstName("John");
        invalidScoringData.setLastName("Doe");
        invalidScoringData.setAmount(BigDecimal.valueOf(-5000));  // Неверная сумма
        invalidScoringData.setTerm(12);
        invalidScoringData.setBirthDate(LocalDate.of(1990, 1, 1));
        invalidScoringData.setPassportSeries("invalid_passport");  // Неверный паспорт
        invalidScoringData.setPassportNumber("12345");
    }

    @Test
    public void testValidateLoanStatementRequestDto_valid() {
        // Подготовка данных для теста
        LoanStatementRequestDto validRequest = new LoanStatementRequestDto();
        validRequest.setPassportSeries("123456");
        validRequest.setPassportNumber("12345");
        validRequest.setAmount(new BigDecimal("100000"));
        validRequest.setTerm(12);
        validRequest.setFirstName("John");
        validRequest.setLastName("Doe");
        validRequest.setMiddleName("Aev");
        validRequest.setEmail("john.doe@example.com");
        validRequest.setBirthDate(LocalDate.of(1990, 1, 1));

        // Мокирование статических и других методов с помощью Mockito
        try (MockedStatic<PrescoringRules> mockedStatic = mockStatic(PrescoringRules.class)) {
            // Определение поведения мока для статического метода
            mockedStatic.when(() -> PrescoringRules.validatePassport(validRequest.getPassportSeries(), validRequest.getPassportNumber()))
                    .thenReturn(true);

            // Мокирование других зависимостей
            mockedStatic.when(() -> PrescoringRules.validateName(validRequest.getFirstName())).thenReturn(true);
            mockedStatic.when(() -> PrescoringRules.validateName(validRequest.getLastName())).thenReturn(true);
            mockedStatic.when(() -> PrescoringRules.validateName(validRequest.getMiddleName())).thenReturn(true);
            mockedStatic.when(() -> PrescoringRules.validateCreditAmount(validRequest.getAmount())).thenReturn(true);
            mockedStatic.when(() -> PrescoringRules.validateLoanTerm(validRequest.getTerm())).thenReturn(true);
            mockedStatic.when(() -> PrescoringRules.validateBirthDate(validRequest.getBirthDate())).thenReturn(true);
            mockedStatic.when(() -> PrescoringRules.validateEmail(validRequest.getEmail())).thenReturn(true);

            // Вызов метода, который использует статические методы
            boolean result = prescoringService.validate(validRequest);

            // Проверка результата
            assertTrue(result);

            // Проверка, что методы были вызваны с ожидаемыми параметрами
            mockedStatic.verify(() -> PrescoringRules.validatePassport("123456", "12345"));
            mockedStatic.verify(() -> PrescoringRules.validateName("John"));
            mockedStatic.verify(() -> PrescoringRules.validateName("Doe"));
            mockedStatic.verify(() -> PrescoringRules.validateName("Aev"));
            mockedStatic.verify(() -> PrescoringRules.validateCreditAmount(validRequest.getAmount()));
            mockedStatic.verify(() -> PrescoringRules.validateLoanTerm(validRequest.getTerm()));
            mockedStatic.verify(() -> PrescoringRules.validateBirthDate(validRequest.getBirthDate()));
            mockedStatic.verify(() -> PrescoringRules.validateEmail(validRequest.getEmail()));
        }
    }

    @Test
    public void testValidateLoanStatementRequestDto_invalid() {
        // Подготовка данных для теста с невалидными значениями
        LoanStatementRequestDto invalidRequest = new LoanStatementRequestDto();
        invalidRequest.setPassportSeries("123456");
        invalidRequest.setPassportNumber("12345");
        invalidRequest.setAmount(new BigDecimal("-5000"));  // Неверная сумма
        invalidRequest.setTerm(12);
        invalidRequest.setFirstName("John");
        invalidRequest.setLastName("Doe");
        invalidRequest.setMiddleName("Middle");
        invalidRequest.setEmail("invalid_email");  // Неверный email
        invalidRequest.setBirthDate(LocalDate.of(1990, 1, 1));

        // Мокирование зависимостей
        try (MockedStatic<PrescoringRules> mockedStatic = mockStatic(PrescoringRules.class)) {
            mockedStatic.when(() -> PrescoringRules.validateName(invalidRequest.getFirstName())).thenReturn(true);
            mockedStatic.when(() -> PrescoringRules.validateName(invalidRequest.getLastName())).thenReturn(true);
            mockedStatic.when(() -> PrescoringRules.validateName(invalidRequest.getMiddleName())).thenReturn(true);
            mockedStatic.when(() -> PrescoringRules.validateCreditAmount(invalidRequest.getAmount())).thenReturn(false);  // Неверная сумма
            mockedStatic.when(() -> PrescoringRules.validateLoanTerm(invalidRequest.getTerm())).thenReturn(true);
            mockedStatic.when(() -> PrescoringRules.validateBirthDate(invalidRequest.getBirthDate())).thenReturn(true);
            mockedStatic.when(() -> PrescoringRules.validateEmail(invalidRequest.getEmail())).thenReturn(false);  // Неверный email
            mockedStatic.when(() -> PrescoringRules.validatePassport(invalidRequest.getPassportSeries(), invalidRequest.getPassportNumber())).thenReturn(true);

            // Вызов метода
            boolean result = prescoringService.validate(invalidRequest);

            // Проверка результата
            assertFalse(result); // Ожидаем, что результат будет false, так как есть ошибки в данных
        }
    }

    @Test
    public void testValidateScoringDataDto_valid() {
        // Подготовка данных для теста с валидными значениями
        ScoringDataDto validScoringData = new ScoringDataDto();
        validScoringData.setPassportSeries("123456");
        validScoringData.setPassportNumber("12345");
        validScoringData.setAmount(new BigDecimal("5000"));
        validScoringData.setTerm(12);
        validScoringData.setFirstName("John");
        validScoringData.setLastName("Doe");
        validScoringData.setMiddleName("Middle");
        validScoringData.setBirthDate(LocalDate.of(1990, 1, 1));

        // Мокирование зависимостей
        try (MockedStatic<PrescoringRules> mockedStatic = mockStatic(PrescoringRules.class)) {
            mockedStatic.when(() -> PrescoringRules.validateName(validScoringData.getFirstName())).thenReturn(true);
            mockedStatic.when(() -> PrescoringRules.validateName(validScoringData.getLastName())).thenReturn(true);
            mockedStatic.when(() -> PrescoringRules.validateCreditAmount(validScoringData.getAmount())).thenReturn(true);
            mockedStatic.when(() -> PrescoringRules.validateLoanTerm(validScoringData.getTerm())).thenReturn(true);
            mockedStatic.when(() -> PrescoringRules.validateBirthDate(validScoringData.getBirthDate())).thenReturn(true);
            mockedStatic.when(() -> PrescoringRules.validatePassport(validScoringData.getPassportSeries(), validScoringData.getPassportNumber())).thenReturn(true);

            // Вызов метода
            boolean result = prescoringService.validate(validScoringData);

            // Проверка результата
            assertTrue(result); // Ожидаем, что результат будет true, так как все проверки прошли успешно
        }
    }

    @Test
    public void testValidateScoringDataDto_invalid() {
        // Подготовка данных для теста с некорректными значениями
        ScoringDataDto invalidScoringData = new ScoringDataDto();
        invalidScoringData.setPassportSeries("123456");
        invalidScoringData.setPassportNumber("12345");
        invalidScoringData.setAmount(new BigDecimal("-5000")); // Неверная сумма
        invalidScoringData.setTerm(12);
        invalidScoringData.setFirstName("John");
        invalidScoringData.setLastName("Doe");
        invalidScoringData.setMiddleName("Middle");
        invalidScoringData.setBirthDate(LocalDate.of(1990, 1, 1));

        // Мокирование зависимостей
        try (MockedStatic<PrescoringRules> mockedStatic = mockStatic(PrescoringRules.class)) {
            mockedStatic.when(() -> PrescoringRules.validateName(invalidScoringData.getFirstName())).thenReturn(true);
            mockedStatic.when(() -> PrescoringRules.validateName(invalidScoringData.getLastName())).thenReturn(true);
            mockedStatic.when(() -> PrescoringRules.validateCreditAmount(invalidScoringData.getAmount())).thenReturn(false);  // Неверная сумма
            mockedStatic.when(() -> PrescoringRules.validateLoanTerm(invalidScoringData.getTerm())).thenReturn(true);
            mockedStatic.when(() -> PrescoringRules.validateBirthDate(invalidScoringData.getBirthDate())).thenReturn(true);
            mockedStatic.when(() -> PrescoringRules.validatePassport(invalidScoringData.getPassportSeries(), invalidScoringData.getPassportNumber())).thenReturn(true);

            // Вызов метода
            boolean result = prescoringService.validate(invalidScoringData);

            // Проверка результата
            assertFalse(result); // Ожидаем, что результат будет false, так как сумма неверна
        }
    }
}


