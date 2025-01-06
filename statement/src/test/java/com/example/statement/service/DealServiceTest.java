package com.example.statement.service;

import com.example.statement.dto.LoanOfferDto;
import com.example.statement.dto.LoanStatementRequestDto;
import com.example.statement.feignclient.OfferClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DealServiceTest {

    @Mock
    private OfferClient statementClient;

    @InjectMocks
    private DealService dealService;

    private LoanStatementRequestDto loanStatementRequestDto;
    private LoanOfferDto loanOfferDto;

    @BeforeEach
    public void setUp() {
        // Инициализация тестовых данных
        loanStatementRequestDto = new LoanStatementRequestDto();
        loanStatementRequestDto.setAmount(BigDecimal.valueOf(100000));
        loanStatementRequestDto.setTerm(12);
        loanStatementRequestDto.setFirstName("Ivan");
        loanStatementRequestDto.setLastName("Ivanov");
        loanStatementRequestDto.setBirthDate(LocalDate.of(1990, 1, 1));
        loanStatementRequestDto.setPassportSeries("1234");
        loanStatementRequestDto.setPassportNumber("567890");
        loanStatementRequestDto.setEmail("ivanov@example.com");

        loanOfferDto = LoanOfferDto.builder()
                .statementId(UUID.fromString("3b43a529-cd1e-42c5-be83-850e9aecc01e"))
                .requestedAmount(BigDecimal.valueOf(50000))
                .totalAmount(BigDecimal.valueOf(50000))
                .term(24)
                .monthlyPayment(BigDecimal.valueOf(2284.24))
                .rate(BigDecimal.valueOf(9.0))
                .build();
    }

    @Test
    public void testSendDealStatement() {
        // Подготовка
        List<LoanOfferDto> expectedOffers = List.of(loanOfferDto);
        when(statementClient.calculateLoanOffers(loanStatementRequestDto)).thenReturn(expectedOffers);

        // Вызов метода
        List<LoanOfferDto> actualOffers = dealService.sendDealStatement(loanStatementRequestDto);

        // Проверка
        assertEquals(expectedOffers, actualOffers);
        verify(statementClient, times(1)).calculateLoanOffers(loanStatementRequestDto);
    }

    @Test
    public void testSelectOffer() {
        // Вызов метода
        dealService.selectOffer(loanOfferDto);

        // Проверка
        verify(statementClient, times(1)).selectOffer(loanOfferDto);
    }
}
