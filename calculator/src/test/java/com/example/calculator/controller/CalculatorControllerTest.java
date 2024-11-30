package com.example.calculator.controller;

import com.example.calculator.dto.CreditDto;
import com.example.calculator.dto.LoanOfferDto;
import com.example.calculator.dto.LoanStatementRequestDto;
import com.example.calculator.dto.ScoringDataDto;
import com.example.calculator.service.LoanCalcService;
import com.example.calculator.service.LoanOfferService;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CalculatorController.class)
@AutoConfigureMockMvc
public class CalculatorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoanCalcService loanCalcService;

    @MockBean
    private LoanOfferService loanOfferService;

    @Test
    void testOffers_Success() throws Exception {
        LoanStatementRequestDto request = new LoanStatementRequestDto();
        request.setAmount(new BigDecimal("100000"));
        request.setTerm(12);

        LoanOfferDto offer = new LoanOfferDto();
        offer.setRequestedAmount(new BigDecimal("100000"));
        offer.setTotalAmount(new BigDecimal("200000"));
        offer.setRate(new BigDecimal("9"));

        when(loanCalcService.calculateCredit(any(ScoringDataDto.class)))
                .thenThrow(new IllegalArgumentException("Invalid data"));

        mockMvc.perform(MockMvcRequestBuilders.post("/calculator/calc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":100000, \"term\":12}"))
                .andExpect(status().isOk())  // Проверка, что статус ответа OK
                .andExpect(MockMvcResultMatchers.jsonPath("$.monthlyPayment").doesNotExist());  // Проверка, что поле "monthlyPayment" отсутствует
    }

    @Test
    void testOffers_Error() throws Exception {
        // Мокаем поведение сервисного слоя, чтобы он выбрасывал исключение
        when(loanOfferService.calculateLoanOffers(any(LoanStatementRequestDto.class)))
                .thenThrow(new IllegalArgumentException("Invalid request"));

        // Выполняем запрос и проверяем ответ
        mockMvc.perform(post("/calculator/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":100000, \"term\":12}"))
                .andExpect(status().isOk())  // Проверка, что статус 200 OK
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(0)));  // Проверка, что результат пуст (массив с нулем элементов)
    }

    @Test
    void testCalculateCredit_Success() throws Exception {
        ScoringDataDto scoringData = new ScoringDataDto();
        scoringData.setAmount(new BigDecimal("100000"));
        scoringData.setTerm(12);

        CreditDto creditDto = new CreditDto();
        creditDto.setMonthlyPayment(new BigDecimal("15000"));

        when(loanCalcService.calculateCredit(any(ScoringDataDto.class)))
                .thenReturn(creditDto);

        mockMvc.perform(post("/calculator/calc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":100000, \"term\":12}"))
                .andExpect(status().isOk())  // Используем ResultMatcher для проверки статуса
                .andExpect(MockMvcResultMatchers.jsonPath("$.monthlyPayment").value("15000"));  // Проверяем, что в ответе есть поле monthlyPayment
    }

    @Test
    void testCalculateCredit_Error() throws Exception {
        when(loanCalcService.calculateCredit(any(ScoringDataDto.class)))
                .thenThrow(new IllegalArgumentException("Invalid data"));

        mockMvc.perform(post("/calculator/calc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":100000, \"term\":12}"))
                .andExpect(status().isOk())  // Используем ResultMatcher для проверки статуса
                .andExpect(MockMvcResultMatchers.jsonPath("$.monthlyPayment").doesNotExist());  // Проверяем, что в ответе нет поля monthlyPayment
    }
}
