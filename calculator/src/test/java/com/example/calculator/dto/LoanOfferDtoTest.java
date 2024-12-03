package com.example.calculator.dto;

import com.example.calculator.dto.LoanOfferDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class LoanOfferDtoTest {

    @Test
    public void testLoanOfferDtoConstructorAndGetters() {
        // Создаем объект LoanOfferDto с тестовыми данными
        UUID statementId = UUID.randomUUID();
        BigDecimal requestedAmount = new BigDecimal("50000");
        BigDecimal totalAmount = new BigDecimal("55000");
        Integer term = 12;
        BigDecimal monthlyPayment = new BigDecimal("4583.33");
        BigDecimal rate = new BigDecimal("10.0");
        Boolean isInsuranceEnabled = true;
        Boolean isSalaryClient = false;

        LoanOfferDto loanOfferDto = new LoanOfferDto();
        loanOfferDto.setStatementId(statementId);
        loanOfferDto.setRequestedAmount(requestedAmount);
        loanOfferDto.setTotalAmount(totalAmount);
        loanOfferDto.setTerm(term);
        loanOfferDto.setMonthlyPayment(monthlyPayment);
        loanOfferDto.setRate(rate);
        loanOfferDto.setIsInsuranceEnabled(isInsuranceEnabled);
        loanOfferDto.setIsSalaryClient(isSalaryClient);

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
        LoanOfferDto loanOfferDto = new LoanOfferDto();

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
