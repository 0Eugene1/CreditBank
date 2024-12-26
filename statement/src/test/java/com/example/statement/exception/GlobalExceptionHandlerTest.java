package com.example.statement.exception;

import com.example.statement.dto.LoanStatementRequestDto;
import com.example.statement.service.LoanStatementService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GlobalExceptionHandler.class)
public class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private LoanStatementService loanStatementService;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Autowired
    private ObjectMapper objectMapper;

    private LoanStatementRequestDto validRequest;
    private LoanStatementRequestDto invalidRequest;
    private BindingResult mockBindingResult;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Общие данные
        validRequest = LoanStatementRequestDto.builder()
                .amount(BigDecimal.valueOf(50000))
                .term(24)
                .firstName("Ivan")
                .lastName("Ivanov")
                .birthDate(LocalDate.of(1990, 1, 1))
                .email("ivanov@example.com")
                .passportSeries("1234")
                .passportNumber("567890")
                .build();

        invalidRequest = LoanStatementRequestDto.builder()
                .amount(BigDecimal.valueOf(-1000))
                .term(-10)
                .firstName("Evgen")
                .build();

        // Мок BindingResult для теста с MethodArgumentNotValidException
        mockBindingResult = mock(BindingResult.class);
    }

    @Test
    public void shouldReturnInternalServerErrorForIllegalArgumentException() throws Exception {
        // Симулируем возникновение IllegalArgumentException
        doThrow(new IllegalArgumentException("Amount and term must not be negative")).when(loanStatementService).processLoanStatement(any());

        mockMvc.perform(post("/statement/statement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Internal server error"));
    }

    @Test
    public void shouldReturnInternalServerErrorForMethodArgumentNotValidException() throws Exception {
        // Отправляем некорректный запрос, который вызовет MethodArgumentNotValidException
        String invalidJson = "{ \"amount\": -2000, \"term\": 24, \"firstName\": \"Evgen\" }";  // Пример некорректных данных

        mockMvc.perform(post("/statement/statement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isInternalServerError())  // Ожидаем статус 500
                .andExpect(content().string("Internal server error"));  // Ожидаем сообщение "Internal server error"
    }


    @Test
    void shouldReturnInternalServerErrorForGeneralException() throws Exception {
        // Симулируем возникновение RuntimeException
        doThrow(new RuntimeException("General exception")).when(loanStatementService).processLoanStatement(validRequest);

        mockMvc.perform(post("/statement/statement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$").value("Internal server error"));
    }
}
