package com.example.statement.service;

import com.example.statement.dto.LoanOfferDto;
import com.example.statement.dto.LoanStatementRequestDto;
import com.example.statement.prescoring.PrescoringRules;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class LoanStatementServiceTest {

    @InjectMocks
    private LoanStatementService loanStatementService;

    @Mock
    private DealService dealService;

    @Mock
    private PrescoringRules prescoringRules;

    private LoanStatementRequestDto requestDto;
    private LoanOfferDto offer1;
    private LoanOfferDto offer2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Общие тестовые данные
        requestDto = LoanStatementRequestDto.builder()
                .amount(BigDecimal.valueOf(50000))
                .term(24)
                .firstName("Ivan")
                .lastName("Ivanov")
                .birthDate(LocalDate.of(1990, 1, 1))
                .email("ivanov@example.com")
                .passportSeries("1234")
                .passportNumber("567890")
                .build();

        offer1 = LoanOfferDto.builder()
                .statementId(UUID.randomUUID())
                .requestedAmount(new BigDecimal(50000))
                .totalAmount(new BigDecimal(50000))
                .term(24)
                .monthlyPayment(new BigDecimal("2284.24"))
                .rate(new BigDecimal("9.0"))
                .build();

        offer2 = LoanOfferDto.builder()
                .statementId(UUID.randomUUID())
                .requestedAmount(new BigDecimal(50000))
                .totalAmount(new BigDecimal(50000))
                .term(24)
                .monthlyPayment(new BigDecimal("2307.25"))
                .rate(new BigDecimal("10.0"))
                .build();

        // Мокируем поведение метода dealService
        when(dealService.sendDealStatement(requestDto)).thenReturn(List.of(offer1, offer2));
    }

    @Test
    void testProcessLoanStatement() {
        // Вызов метода
        List<LoanOfferDto> loanOffers = loanStatementService.processLoanStatement(requestDto);

        // Проверка результата
        assertEquals(2, loanOffers.size(), "The number of loan offers should be 2");
        assertEquals(10.0, loanOffers.get(0).getRate().doubleValue(), "The first offer should have the highest rate");
        assertEquals(9.0, loanOffers.get(1).getRate().doubleValue(), "The second offer should have the lower rate");
    }
}
