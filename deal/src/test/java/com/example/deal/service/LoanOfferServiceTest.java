package com.example.deal.service;

import com.example.deal.dto.EmploymentDto;
import com.example.deal.dto.FinishRegistrationRequestDto;
import com.example.deal.dto.LoanOfferDto;
import com.example.deal.dto.LoanStatementRequestDto;
import com.example.deal.entity.Client;
import com.example.deal.entity.Credit;
import com.example.deal.entity.Statement;
import com.example.deal.enums.*;
import com.example.deal.mccalculator.CalculatorClient;
import com.example.deal.repository.ClientRepository;
import com.example.deal.repository.CreditRepository;
import com.example.deal.repository.StatementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
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
    private CalculatorClient calculatorClient;

    @InjectMocks
    private LoanOfferService loanOfferService;

    private LoanStatementRequestDto request;
    private Client mockClient;
    private Statement mockStatement;
    private LoanOfferDto mockLoanOffer;
    private FinishRegistrationRequestDto registrationRequest;
    private LoanStatementRequestDto loanStatementRequestDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Инициализация объектов
        request = LoanStatementRequestDto.builder()
                .amount(new BigDecimal("10000"))
                .term(12)
                .firstName("John")
                .lastName("Doe")
                .middleName("MiddleName")
                .email("john.doe@example.com")
                .birthDate(LocalDate.of(1990, 1, 1))
                .passportSeries("AA")
                .passportNumber("123456")
                .build();

        mockClient = new Client();
        mockClient.setFirstName(request.getFirstName());
        mockClient.setLastName(request.getLastName());

        mockStatement = Statement.builder()
                .statementId(UUID.randomUUID())
                .client(mockClient)
                .status(ApplicationStatus.PREAPPROVAL)
                .creationDate(LocalDateTime.now())
                .appliedOffer("{}")
                .statusHistory("{}")
                .build();

        mockLoanOffer = LoanOfferDto.builder()
                .totalAmount(BigDecimal.valueOf(10000.0))
                .statementId(mockStatement.getStatementId())
                .build();

        // Заводим данные для FinishRegistrationRequestDto
        registrationRequest = FinishRegistrationRequestDto.builder()
                .gender(Gender.MALE)
                .maritalStatus(MaritalStatus.SINGLE)
                .dependentAmount(2)
                .passportIssueDate(LocalDate.of(2000, 1, 1))
                .passportIssueBranch("Branch")
                .employment(EmploymentDto.builder().build())
                .accountNumber("1234567890")
                .build();

        // Заводим данные для LoanStatementRequestDto
        loanStatementRequestDto = LoanStatementRequestDto.builder()
                .amount(BigDecimal.valueOf(30000.0))
                .term(12)
                .firstName("John")
                .lastName("Doe")
                .middleName("Middle")
                .email("johndoe@example.com")
                .birthDate(LocalDate.of(1985, 5, 15))
                .passportSeries("AB")
                .passportNumber("123456")
                .build();
    }

    @Test
    void testCreateClientFromRequest_success() {
        // Моки
        Statement mockStatement = Statement.builder().build();
        mockStatement.setStatementId(UUID.randomUUID());

        LoanOfferDto mockLoanOffer = LoanOfferDto.builder().build();
        mockLoanOffer.setTotalAmount(BigDecimal.valueOf(10000.0));
        mockLoanOffer.setStatementId(mockStatement.getStatementId());


        when(clientRepository.save(any(Client.class))).thenReturn(mockClient);
        when(creditRepository.save(any(Credit.class))).thenReturn(new Credit());
        when(statementRepository.save(any(Statement.class))).thenReturn(mockStatement);
        when(calculatorClient.getLoanOffers(any(LoanStatementRequestDto.class)))
                .thenReturn(Arrays.asList(mockLoanOffer));

        // Вызов метода
        List<LoanOfferDto> result = loanOfferService.createClientFromRequest(request);

        // Проверки
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mockStatement.getStatementId(), result.get(0).getStatementId());
        verify(clientRepository, times(1)).save(any(Client.class));
        verify(statementRepository, times(1)).save(any(Statement.class));
        verify(calculatorClient, times(1)).getLoanOffers(any(LoanStatementRequestDto.class));
    }

    @Test
    void testCreateClientFromRequest_noLoanOffers() {
        // Моки
        when(calculatorClient.getLoanOffers(any(LoanStatementRequestDto.class)))
                .thenReturn(Arrays.asList());

        // Вызов метода и проверка исключения
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            loanOfferService.createClientFromRequest(request);
        });

        assertEquals("Кредитные предложения не могут быть пустыми.", exception.getMessage());
    }
    @Test
    void testCreateClientFromRequest_loanOffersSorted() {
        // Моки
        LoanOfferDto mockLoanOffer1 = LoanOfferDto.builder()
                .totalAmount(BigDecimal.valueOf(20000.0))
                .statementId(mockStatement.getStatementId())
                .build();

        LoanOfferDto mockLoanOffer2 = LoanOfferDto.builder()
                .totalAmount(BigDecimal.valueOf(10000.0))
                .statementId(mockStatement.getStatementId())
                .build();


        when(clientRepository.save(any(Client.class))).thenReturn(mockClient);
        when(creditRepository.save(any(Credit.class))).thenReturn(new Credit());
        when(statementRepository.save(any(Statement.class))).thenReturn(mockStatement);
        when(calculatorClient.getLoanOffers(any(LoanStatementRequestDto.class)))
                .thenReturn(Arrays.asList(mockLoanOffer1, mockLoanOffer2));

        // Вызов метода
        List<LoanOfferDto> result = loanOfferService.createClientFromRequest(loanStatementRequestDto);

        // Проверка, что список отсортирован по totalAmount
        assertEquals(0, BigDecimal.valueOf(10000.0).compareTo(result.get(0).getTotalAmount()));
        assertEquals(0, BigDecimal.valueOf(20000.0).compareTo(result.get(1).getTotalAmount()));
    }
}
