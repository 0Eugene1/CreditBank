package com.example.calculator.service;

import com.example.calculator.dto.LoanStatementRequestDto;
import com.example.calculator.dto.ScoringDataDto;
import com.example.calculator.prescoring.PrescoringRules;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PrescoringServiceTest {

    @InjectMocks
    private PrescoringService prescoringService;

    private LoanStatementRequestDto validRequest;
    private LoanStatementRequestDto invalidRequest;

    private ScoringDataDto validScoringData;
    private ScoringDataDto invalidScoringData;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Инициализация глобальных объектов
        validRequest = LoanStatementRequestDto.builder()
                .amount(new BigDecimal("100000"))
                .term(12)
                .firstName("John")
                .lastName("Doe")
                .middleName("Aev")
                .email("john.doe@example.com")
                .birthDate(LocalDate.of(1990, 1, 1))
                .passportSeries("123456")
                .passportNumber("12345")
                .build();

        invalidRequest = LoanStatementRequestDto.builder()
                .amount(new BigDecimal("100000"))
                .term(12)
                .firstName("John")
                .lastName("Doe")
                .middleName("Aev")
                .email("john.doe@example.com")
                .birthDate(LocalDate.of(1990, 1, 1))
                .passportSeries("123456")
                .passportNumber("12345")
                .build();

        validScoringData = ScoringDataDto.builder()
                .passportSeries("123456")
                .passportNumber("12345")
                .amount(new BigDecimal("5000"))
                .term(12)
                .firstName("John")
                .lastName("Doe")
                .middleName("Middle")
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        invalidScoringData = ScoringDataDto.builder()
                .passportNumber("12345")
                .passportSeries("123456")
                .amount(new BigDecimal("-5000")) // Неверная сумма
                .term(12)
                .firstName("John")
                .lastName("Doe")
                .middleName("Middle")
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();
    }


    @Test
    public void testValidateLoanStatementRequestDto_valid() {

        try (MockedStatic<PrescoringRules> mockedStatic = mockStatic(PrescoringRules.class)) {
            // Настраиваем моки только для проверки вызовов и предотвращения реального выполнения
            mockedStatic.when(() -> PrescoringRules.validatePassport(validRequest.getPassportSeries(), validRequest.getPassportNumber()))
                    .thenAnswer(invocation -> null); // Эмулируем нормальную работу (void)
            mockedStatic.when(() -> PrescoringRules.validateBirthDate(validRequest.getBirthDate()))
                    .thenAnswer(invocation -> null);

            // Убедимся, что метод выполняется без исключений
            assertDoesNotThrow(() -> prescoringService.validate(validRequest));
        }

    }

    @Test
    public void testValidateLoanStatementRequestDto_invalid() {

        try (MockedStatic<PrescoringRules> mockedStatic = mockStatic(PrescoringRules.class)) {
            mockedStatic.when(() -> PrescoringRules.validateName(anyString())).then(invocation -> null);
            mockedStatic.when(() -> PrescoringRules.validateCreditAmount(any(BigDecimal.class)))
                    .thenThrow(new IllegalArgumentException("Invalid validate"));
            mockedStatic.when(() -> PrescoringRules.validateLoanTerm(anyInt())).then(invocation -> null);
            mockedStatic.when(() -> PrescoringRules.validateBirthDate(any(LocalDate.class))).then(invocation -> null);
            mockedStatic.when(() -> PrescoringRules.validatePassport(anyString(), anyString())).then(invocation -> null);

            assertThrows(IllegalArgumentException.class, () -> prescoringService.validate(invalidRequest));
        }
    }

    @Test
    public void testValidateScoringDataDto_valid() {

        // Мокирование зависимостей
        try (MockedStatic<PrescoringRules> mockedStatic = mockStatic(PrescoringRules.class)) {
            mockedStatic.when(() -> PrescoringRules.validateName(anyString())).then(invocation -> null); // Метод ничего не делает
            mockedStatic.when(() -> PrescoringRules.validateCreditAmount(any(BigDecimal.class))).then(invocation -> null);
            mockedStatic.when(() -> PrescoringRules.validateLoanTerm(anyInt())).then(invocation -> null);
            mockedStatic.when(() -> PrescoringRules.validateBirthDate(any(LocalDate.class))).then(invocation -> null);
            mockedStatic.when(() -> PrescoringRules.validatePassport(anyString(), anyString())).then(invocation -> null);

            assertDoesNotThrow(() -> prescoringService.validate(validScoringData));
        }
    }

    @Test
    public void testValidateScoringDataDto_invalid() {

        try (MockedStatic<PrescoringRules> mockedStatic = mockStatic(PrescoringRules.class)) {
            mockedStatic.when(() -> PrescoringRules.validateName(anyString())).then(invocation -> null);
            mockedStatic.when(() -> PrescoringRules.validateCreditAmount(any(BigDecimal.class)))
                    .thenThrow(new IllegalArgumentException("Invalid credit amount"));
            mockedStatic.when(() -> PrescoringRules.validateLoanTerm(anyInt())).then(invocation -> null);
            mockedStatic.when(() -> PrescoringRules.validateBirthDate(any(LocalDate.class))).then(invocation -> null);
            mockedStatic.when(() -> PrescoringRules.validatePassport(anyString(), anyString())).then(invocation -> null);

            assertThrows(IllegalArgumentException.class, () -> prescoringService.validate(invalidScoringData));
        }
    }
}



