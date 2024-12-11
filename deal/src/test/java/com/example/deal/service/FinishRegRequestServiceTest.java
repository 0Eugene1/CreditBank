package com.example.deal.service;

import com.example.deal.dto.*;
import com.example.deal.entity.Client;
import com.example.deal.entity.Credit;
import com.example.deal.entity.Statement;
import com.example.deal.enums.*;
import com.example.deal.exception.StatementNotFoundException;
import com.example.deal.mccalculator.CalculatorClient;
import com.example.deal.repository.ClientRepository;
import com.example.deal.repository.CreditRepository;
import com.example.deal.repository.StatementRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@RequiredArgsConstructor
class FinishRegRequestServiceTest {

    @Mock
    private CreditRepository creditRepository;

    @Mock
    private StatementRepository statementRepository;

    @Mock
    private CalculatorClient calculateCredit;

    @InjectMocks
    private FinishRegRequestService finishRegRequestService;

    private FinishRegistrationRequestDto registrationRequest;

    private ClientRepository clientRepository;

    private CalculatorClient calculatorClient;

    private LoanOfferService loanOfferService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);


        EmploymentDto employmentDto = EmploymentDto.builder()
                .employmentStatus(EmploymentStatus.EMPLOYED)
                .employerINN("1234567890")
                .salary(BigDecimal.valueOf(50000))
                .position(EmploymentPosition.WORKER)
                .workExperienceTotal(5)
                .workExperienceCurrent(2)
                .build();

        registrationRequest = FinishRegistrationRequestDto.builder()
                        .accountNumber("1234567890")
                .gender(Gender.MALE)
                .employment(employmentDto)
                .maritalStatus(MaritalStatus.MARRIED)
                .dependentAmount(0)
                .passportIssueBrach("Branch A")
                .passportIssueDate(null)
                .build();

    }

  @Test
    void testFinishRegistration_statementNotFound() {
        // Моки
        when(statementRepository.findById(any(UUID.class))).thenReturn(java.util.Optional.empty());

        // Вызов метода и проверка исключения
        StatementNotFoundException exception = assertThrows(StatementNotFoundException.class, () -> {
            finishRegRequestService.finishRegistration(UUID.randomUUID().toString(), registrationRequest);
        });

        assertEquals("Statement not found", exception.getMessage());
    }
}
