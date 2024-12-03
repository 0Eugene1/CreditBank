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
    }


        @Test
        public void testValidateLoanStatementRequestDto_valid() {
            // Подготовка данных для теста
            LoanStatementRequestDto validRequest = LoanStatementRequestDto.builder()
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
        public void testValidateLoanStatementRequestDto_invalid () {

            // Подготовка данных для теста с невалидными значениями
            LoanStatementRequestDto invalidRequest  = LoanStatementRequestDto.builder()
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
        public void testValidateScoringDataDto_valid () {
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
                mockedStatic.when(() -> PrescoringRules.validateName(anyString())).then(invocation -> null); // Метод ничего не делает
                mockedStatic.when(() -> PrescoringRules.validateCreditAmount(any(BigDecimal.class))).then(invocation -> null);
                mockedStatic.when(() -> PrescoringRules.validateLoanTerm(anyInt())).then(invocation -> null);
                mockedStatic.when(() -> PrescoringRules.validateBirthDate(any(LocalDate.class))).then(invocation -> null);
                mockedStatic.when(() -> PrescoringRules.validatePassport(anyString(), anyString())).then(invocation -> null);

                assertDoesNotThrow(() -> prescoringService.validate(validScoringData));
            }
        }

        @Test
        public void testValidateScoringDataDto_invalid () {
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



