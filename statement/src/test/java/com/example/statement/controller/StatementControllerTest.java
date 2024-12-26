package com.example.statement.controller;

import com.example.statement.dto.LoanOfferDto;
import com.example.statement.dto.LoanStatementRequestDto;
import com.example.statement.service.DealService;
import com.example.statement.service.LoanStatementService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StatementController.class)
public class StatementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DealService dealService;

    @MockBean
    private LoanStatementService loanStatementService;

    private LoanOfferDto validOffer;

    @BeforeEach
    void setUp() {
        LoanStatementRequestDto.builder()
                .amount(BigDecimal.valueOf(50000))
                .term(12)
                .firstName("John")
                .lastName("Doe")
                .build();

        validOffer = LoanOfferDto.builder()
                .statementId(UUID.randomUUID())
                .requestedAmount(BigDecimal.valueOf(50000))
                .totalAmount(BigDecimal.valueOf(55000))
                .term(12)
                .monthlyPayment(BigDecimal.valueOf(4583.33))
                .rate(BigDecimal.valueOf(10.0))
                .isInsuranceEnabled(true)
                .isSalaryClient(false)
                .build();

        LoanOfferDto.builder()
                .statementId(UUID.randomUUID())
                .requestedAmount(BigDecimal.valueOf(50000))
                .totalAmount(BigDecimal.valueOf(55000))
                .term(12)
                .monthlyPayment(BigDecimal.valueOf(4583.33))
                .rate(BigDecimal.valueOf(10.0))
                .isInsuranceEnabled(false)
                .isSalaryClient(true)
                .build();
        LoanOfferDto.builder()
                .statementId(UUID.randomUUID())
                .requestedAmount(BigDecimal.valueOf(45000))
                .totalAmount(BigDecimal.valueOf(50000))
                .term(12)
                .monthlyPayment(BigDecimal.valueOf(4166.67))
                .rate(BigDecimal.valueOf(10.0))
                .isInsuranceEnabled(true)
                .isSalaryClient(false)
                .build();
    }



    @Test
    void shouldSelectLoanOfferOnValidRequest() throws Exception {
        doNothing().when(dealService).selectOffer(Mockito.any(LoanOfferDto.class));

        mockMvc.perform(post("/statement/offer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validOffer)))
                .andExpect(status().isOk());

        verify(dealService, times(1)).selectOffer(any(LoanOfferDto.class));
    }

    @Test
    void shouldReturnInternalServerErrorOnOfferSelectionFailure() throws Exception {
        doThrow(new RuntimeException("Test exception")).when(dealService).selectOffer(Mockito.any(LoanOfferDto.class));

        mockMvc.perform(post("/statement/offer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validOffer)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Internal server error"));

        verify(dealService, times(1)).selectOffer(any(LoanOfferDto.class));
    }
}
