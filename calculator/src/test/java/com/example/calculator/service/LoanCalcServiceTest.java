package com.example.calculator.service;


import com.example.calculator.dto.CreditDto;
import com.example.calculator.dto.ScoringDataDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.RequestEntity.post;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


import java.math.BigDecimal;

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

//        doNothing().when(prescoringService).validate(scoringData);
        when(scoringService.calculateRate(scoringData)).thenReturn(5.0);

        // Ожидаемое значение ежемесячного платежа после расчетов
        BigDecimal expectedMonthlyPayment = BigDecimal.valueOf(45129.16);  // Ожидаемое значение

        CreditDto creditDto = loanCalcService.calculateCredit(scoringData);

        // Проверка результата
        assertEquals(expectedMonthlyPayment, creditDto.getMonthlyPayment());
    }


    @Test
    public void calculateCredit_prescoringFails() throws Exception {
        // Подготовка входных данных
        ScoringDataDto scoringData = new ScoringDataDto();
        scoringData.setAmount(new BigDecimal("500000"));
        scoringData.setTerm(12);

        // Настройка моков для prescoringService
        doNothing().when(prescoringService).validate(any(ScoringDataDto.class));

        // Мокаем поведение сервиса, чтобы вызвать исключение
        when(scoringService.calculateRate(any(ScoringDataDto.class)))
                .thenThrow(new IllegalArgumentException("Invalid data"));

        // Выполняем запрос и проверяем, что статус 400 и ошибка в теле ответа
        mockMvc.perform(MockMvcRequestBuilders.post("/calculator/calc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":100000, \"term\":12, \"firstName\": \"John\", \"lastName\": \"Doe\"}"))
                .andExpect(status().isBadRequest())  // Проверка на статус 400
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value(
                        containsString("passportNumber: Номер паспорта не может быть пустым")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value(
                        containsString("birthDate: Поле минимальный возраст не может быть пустым")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value(
                        containsString("passportSeries: Серия паспорта не может быть пустой")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400));  // Проверка статуса
    }



    @Test
    void calculateCredit_largeLoan() {
        ScoringDataDto scoringData = new ScoringDataDto();
        scoringData.setAmount(BigDecimal.valueOf(10000000));
        scoringData.setTerm(24);
        when(scoringService.calculateRate(scoringData)).thenReturn(8.0);

        CreditDto creditDto = loanCalcService.calculateCredit(scoringData);

        assertNotNull(creditDto);
        assertTrue(creditDto.getPsk().compareTo(BigDecimal.ZERO) > 0);
    }

}
