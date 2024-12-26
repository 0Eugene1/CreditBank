package com.example.calculator.service;


import com.example.calculator.dto.CreditDto;
import com.example.calculator.dto.ScoringDataDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class LoanCalcServiceTest {

    @InjectMocks
    private LoanCalcService loanCalcService;

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ScoringService scoringService;

    @Mock
    private PrescoringService prescoringService;

    private ScoringDataDto scoringData;

    @BeforeEach
    void setUp() {
        // Инициализация общего объекта ScoringDataDto для всех тестов
        scoringData = ScoringDataDto.builder()
                .amount(BigDecimal.valueOf(500000))
                .term(12)
                .isInsuranceEnabled(false)
                .isSalaryClient(false)
                .build();

        // Значение baseRate через ReflectionTestUtils
        ReflectionTestUtils.setField(loanCalcService, "baseRate", BigDecimal.valueOf(10.0));
    }

    @Test
    void calculateCredit_success() {
        // Мокаем результат расчета ставки
        when(scoringService.calculateRate(scoringData)).thenReturn(BigDecimal.valueOf(5.0));

        // Ожидаемое значение ежемесячного платежа после расчетов
        BigDecimal expectedMonthlyPayment = BigDecimal.valueOf(45129.16);

        // Выполняем расчет кредита
        CreditDto creditDto = loanCalcService.calculateCredit(scoringData);

        // Проверка результата
        assertEquals(expectedMonthlyPayment, creditDto.getMonthlyPayment());
    }

    @Test
    public void calculateCredit_prescoringFails() throws Exception {
        // Настройка моков для prescoringService
        doNothing().when(prescoringService).validate(any(ScoringDataDto.class));

        // Мокаем поведение сервиса, чтобы вызвать исключение
        when(scoringService.calculateRate(any(ScoringDataDto.class)))
                .thenThrow(new NullPointerException("Cannot invoke method on null object"));

        // Отправляем запрос с валидными данными
        mockMvc.perform(MockMvcRequestBuilders.post("/calculator/calc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":100000, \"term\":12, \"firstName\": \"Name\", \"lastName\": \"MidName\", \"birthDate\": \"2000-01-01\", \"passportSeries\": \"1234\", \"passportNumber\": \"567890\"}"))
                .andExpect(status().isInternalServerError())  // Ожидаем статус 500
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value(
                        containsString("Произошла внутренняя ошибка")));  // Ожидаемое сообщение об ошибке
    }


    @Test
    void calculateCredit_largeLoan() {
        // Устанавливаем большую сумму кредита
        scoringData.setAmount(BigDecimal.valueOf(10000000));

        // Мокаем расчет ставки
        when(scoringService.calculateRate(scoringData)).thenReturn(BigDecimal.valueOf(8.0));

        // Выполняем расчет кредита
        CreditDto creditDto = loanCalcService.calculateCredit(scoringData);

        // Проверки
        assertNotNull(creditDto);
        assertTrue(creditDto.getPsk().compareTo(BigDecimal.ZERO) > 0);
    }
}

