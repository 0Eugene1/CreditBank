package com.example.calculator.service;


import com.example.calculator.dto.CreditDto;
import com.example.calculator.dto.ScoringDataDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;


import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)
public class LoanCalcServiceTest {

    @InjectMocks
    private LoanCalcService loanCalcService;


    @Mock
    private ScoringService scoringService;

    @Mock
    private PrescoringService prescoringService;

    @BeforeEach
    void setUp() {
        //значение baseRate через ReflectionTestUtils
        ReflectionTestUtils.setField(loanCalcService, "baseRate", 10.0);
    }

    @Test
    void calculateCredit_success() {
        ScoringDataDto scoringData = new ScoringDataDto();
        scoringData.setAmount(BigDecimal.valueOf(500000));
        scoringData.setTerm(12);
        scoringData.setIsInsuranceEnabled(false);
        scoringData.setIsSalaryClient(false);

        when(prescoringService.validate(scoringData)).thenReturn(true);
        when(scoringService.calculateRate(scoringData)).thenReturn(5.0);

        // Ожидаемое значение ежемесячного платежа после расчетов
        BigDecimal expectedMonthlyPayment = BigDecimal.valueOf(45129.16);  // Ожидаемое значение

        CreditDto creditDto = loanCalcService.calculateCredit(scoringData);

        // Проверка результата
        assertEquals(expectedMonthlyPayment, creditDto.getMonthlyPayment());
    }

    @Test
    public void calculateCredit_prescoringFails() {
        // Подготовка входных данных
        ScoringDataDto scoringData = new ScoringDataDto();
        scoringData.setAmount(new BigDecimal("500000"));
        scoringData.setTerm(12);

        // Настройка моков
        when(prescoringService.validate(any(ScoringDataDto.class))).thenReturn(false);  // Симулируем ошибку prescoring

        // Ожидаем исключение
        assertThrows(IllegalArgumentException.class, () -> loanCalcService.calculateCredit(scoringData));
    }

    @Test
    void calculateCredit_largeLoan() {
        ScoringDataDto scoringData = new ScoringDataDto();
        scoringData.setAmount(BigDecimal.valueOf(10000000));
        scoringData.setTerm(24);
        when(prescoringService.validate(scoringData)).thenReturn(true);
        when(scoringService.calculateRate(scoringData)).thenReturn(8.0);

        CreditDto creditDto = loanCalcService.calculateCredit(scoringData);

        assertNotNull(creditDto);
        assertTrue(creditDto.getPsk().compareTo(BigDecimal.ZERO) > 0);
    }

}
