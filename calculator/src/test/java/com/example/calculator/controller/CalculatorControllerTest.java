package com.example.calculator.controller;

import com.example.calculator.dto.CreditDto;
import com.example.calculator.dto.LoanStatementRequestDto;
import com.example.calculator.dto.ScoringDataDto;
import com.example.calculator.service.LoanCalcService;
import com.example.calculator.service.LoanOfferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    private String jsonRequest;
    private CreditDto creditDto;

    @BeforeEach
    void setUp() {
        // Общие данные для всех тестов
        jsonRequest = """
                {
                    "amount": 20000.00,
                    "term": 12,
                    "firstName": "John",
                    "lastName": "Doe",
                    "birthDate": "1990-01-01",
                    "passportSeries": "1234",
                    "passportNumber": "567890"
                }
                """;

        creditDto = CreditDto.builder()
                .monthlyPayment(new BigDecimal("15000"))
                .build();
    }


    @Test
    void testCalculateCredit_Success() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/calculator/calc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk());
    }


    @Test
    void testOffers_Error() throws Exception {
        // Мокаем поведение сервисного слоя, чтобы он выбрасывал исключение
        when(loanOfferService.calculateLoanOffers(any(LoanStatementRequestDto.class)))
                .thenThrow(new IllegalArgumentException("Invalid request"));

        // Выполняем запрос
        mockMvc.perform(post("/calculator/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":100000, \"term\":12}"))
                .andExpect(status().isBadRequest())  // Проверка, что статус 400 Bad Request
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Некорректные входные данные: Invalid request"))  // Проверка, что сообщение ошибки корректное
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400));  // Проверка, что статус в теле ответа равен 400
    }


    @Test
    void testCalculateCredit_Successful() throws Exception {

        // Мокаем вызов сервиса
        when(loanCalcService.calculateCredit(any(ScoringDataDto.class)))
                .thenReturn(creditDto);

        mockMvc.perform(post("/calculator/calc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk());
    }

    @Test
    void testCalculateCredit_Error() throws Exception {
        when(loanCalcService.calculateCredit(any(ScoringDataDto.class)))
                .thenThrow(new IllegalArgumentException("Некорректные входные данные."));

        mockMvc.perform(post("/calculator/calc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "amount": 100000,
                                    "term": 12,
                                    "firstName": "John",
                                    "lastName": "Doe",
                                    "birthDate": "1990-01-01",
                                    "passportSeries": "1234",
                                    "passportNumber": "567890",
                                    "gender": "MALE"
                                }
                                """))
                .andExpect(status().isBadRequest())  // Проверяем, что статус 400
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Некорректные входные данные: Некорректные входные данные."));  // Проверяем сообщение об ошибке
    }


}
