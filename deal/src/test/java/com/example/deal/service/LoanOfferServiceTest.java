package com.example.deal.service;

import com.example.deal.dto.LoanOfferDto;
import com.example.deal.dto.LoanStatementRequestDto;
import com.example.deal.entity.Client;
import com.example.deal.entity.Credit;
import com.example.deal.entity.Statement;
import com.example.deal.feignclient.CalculatorOffersClient;
import com.example.deal.mapper.ClientMapper;
import com.example.deal.mapper.StatementMapper;
import com.example.deal.repository.ClientRepository;
import com.example.deal.repository.CreditRepository;
import com.example.deal.repository.StatementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoanOfferServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private CreditRepository creditRepository;

    @Mock
    private StatementRepository statementRepository;

    @Mock
    private CalculatorOffersClient calculatorOffersClient;

    @Mock
    private ClientMapper clientMapper;

    @Mock
    private StatementMapper statementMapper;

    @InjectMocks
    private LoanOfferService loanOfferService;

    private LoanStatementRequestDto requestDto;
    private Client client;
    private Credit credit;
    private Statement statement;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Инициализация данных для тестов
        requestDto = LoanStatementRequestDto.builder()
                .amount(BigDecimal.valueOf(100000))
                .term(24)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .birthDate(LocalDate.of(1990, 1, 1))
                .passportSeries("1234")
                .passportNumber("567890")
                .build();

        client = new Client();
        client.setClientId(UUID.randomUUID());

        credit = new Credit();
        credit.setCreditId(UUID.randomUUID());

        statement = Statement.builder()
                .statementId(UUID.randomUUID())
                .client(client)
                .credit(credit)
                .build();
    }

    @Test
    void createClientFromRequest_ShouldReturnSortedLoanOffers() {
        // Мокируем зависимости
        when(clientMapper.toEntity(requestDto)).thenReturn(client);
        when(clientRepository.save(client)).thenReturn(client);

        when(statementMapper.toCreditEntity(requestDto)).thenReturn(credit);
        when(creditRepository.save(credit)).thenReturn(credit);

        when(statementMapper.toEntity(requestDto, client, credit)).thenReturn(statement);
        when(statementRepository.save(statement)).thenReturn(statement);

        LoanOfferDto offer1 = LoanOfferDto.builder()
                .totalAmount(BigDecimal.valueOf(200000))
                .statementId(statement.getStatementId())
                .build();

        LoanOfferDto offer2 = LoanOfferDto.builder()
                .totalAmount(BigDecimal.valueOf(150000))
                .statementId(statement.getStatementId())
                .build();

        when(calculatorOffersClient.getLoanOffers(requestDto)).thenReturn(List.of(offer1, offer2));

        // Вызов тестируемого метода
        List<LoanOfferDto> result = loanOfferService.createClientFromRequest(requestDto);

        // Проверяем сортировку
        assertEquals(2, result.size());
        assertEquals(offer2.getTotalAmount(), result.get(0).getTotalAmount());
        assertEquals(offer1.getTotalAmount(), result.get(1).getTotalAmount());

        // Проверяем вызовы зависимостей
        verify(clientRepository).save(client);
        verify(statementRepository).save(statement);
        verify(calculatorOffersClient).getLoanOffers(requestDto);
    }

    @Test
    void createClientFromRequest_ShouldThrowException_WhenLoanOffersEmpty() {
        when(clientMapper.toEntity(requestDto)).thenReturn(client);
        when(clientRepository.save(client)).thenReturn(client);

        when(statementMapper.toCreditEntity(requestDto)).thenReturn(credit);
        when(creditRepository.save(credit)).thenReturn(credit);

        when(statementMapper.toEntity(requestDto, client, credit)).thenReturn(statement);
        when(statementRepository.save(statement)).thenReturn(statement);

        when(calculatorOffersClient.getLoanOffers(requestDto)).thenReturn(List.of());

        // Проверяем, что метод выбрасывает исключение
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> loanOfferService.createClientFromRequest(requestDto));

        assertEquals("Кредитные предложения не могут быть пустыми.", exception.getMessage());
        verify(calculatorOffersClient).getLoanOffers(requestDto);
    }
}
