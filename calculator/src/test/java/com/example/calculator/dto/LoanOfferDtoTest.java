package com.example.calculator.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class LoanOfferDtoTest {

    private UUID statementId;
    private BigDecimal requestedAmount;
    private BigDecimal totalAmount;
    private Integer term;
    private BigDecimal monthlyPayment;
    private BigDecimal rate;
    private Boolean isInsuranceEnabled;
    private Boolean isSalaryClient;

    private LoanOfferDto loanOfferDto;

    @BeforeEach
    public void setUp() {
        // Подготовка данных, которые будут использоваться в тестах
        statementId = UUID.randomUUID();
        requestedAmount = new BigDecimal("50000");
        totalAmount = new BigDecimal("55000");
        term = 12;
        monthlyPayment = new BigDecimal("4583.33");
        rate = new BigDecimal("10.0");
        isInsuranceEnabled = true;
        isSalaryClient = false;

        // Создаем объект LoanOfferDto
        loanOfferDto = LoanOfferDto.builder()
                .statementId(statementId)
                .requestedAmount(requestedAmount)
                .totalAmount(totalAmount)
                .term(term)
                .monthlyPayment(monthlyPayment)
                .rate(rate)
                .isInsuranceEnabled(isInsuranceEnabled)
                .isSalaryClient(isSalaryClient)
                .build();
    }

    @Test
    public void testLoanOfferDtoConstructorAndGetters() {

        // Проверка всех значений
        assertEquals(statementId, loanOfferDto.getStatementId());
        assertEquals(requestedAmount, loanOfferDto.getRequestedAmount());
        assertEquals(totalAmount, loanOfferDto.getTotalAmount());
        assertEquals(term, loanOfferDto.getTerm());
        assertEquals(monthlyPayment, loanOfferDto.getMonthlyPayment());
        assertEquals(rate, loanOfferDto.getRate());
        assertEquals(isInsuranceEnabled, loanOfferDto.getIsInsuranceEnabled());
        assertEquals(isSalaryClient, loanOfferDto.getIsSalaryClient());
    }

    @Test
    public void testLoanOfferDtoDefaultConstructor() {
        // Создаем объект без параметров
        LoanOfferDto loanOfferDto = LoanOfferDto.builder().build();

        // Проверка значений по умолчанию
        assertNull(loanOfferDto.getStatementId());
        assertNull(loanOfferDto.getRequestedAmount());
        assertNull(loanOfferDto.getTotalAmount());
        assertNull(loanOfferDto.getTerm());
        assertNull(loanOfferDto.getMonthlyPayment());
        assertNull(loanOfferDto.getRate());
        assertNull(loanOfferDto.getIsInsuranceEnabled());
        assertNull(loanOfferDto.getIsSalaryClient());
    }
}
