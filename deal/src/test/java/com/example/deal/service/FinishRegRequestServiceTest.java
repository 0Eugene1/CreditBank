package com.example.deal.service;

import com.example.deal.dto.*;
import com.example.deal.entity.Client;

import com.example.deal.enums.*;
import com.example.deal.exception.StatementNotFoundException;
import com.example.deal.repository.ClientRepository;
import com.example.deal.repository.CreditRepository;
import com.example.deal.repository.StatementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@RequiredArgsConstructor
class FinishRegRequestServiceTest {

    @Autowired
    private MockMvc mockMvc;
    @Mock
    private CreditRepository creditRepository;

    @Mock
    private StatementRepository statementRepository;


    @InjectMocks
    private FinishRegRequestService finishRegRequestService;

    private FinishRegistrationRequestDto registrationRequest;

    private ClientRepository clientRepository;


    private LoanOfferService loanOfferService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);


        EmploymentDto employmentDto = EmploymentDto.builder()
                .employmentStatus(EmploymentStatus.EMPLOYED)
                .employerInn("1234567890")
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
                .passportIssueBranch("Branch A")
                .passportIssueDate(null)
                .build();

    }

  @Test
    void testFinishRegistration_statementNotFound() {
        // Моки
        when(statementRepository.findById(any(UUID.class))).thenReturn(java.util.Optional.empty());

        // Вызов метода и проверка исключения
        StatementNotFoundException exception = assertThrows(StatementNotFoundException.class, () -> finishRegRequestService.finishRegistration(UUID.randomUUID().toString(), registrationRequest));

        assertEquals("Statement not found", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPassportIsMissing() {
        // Создаем клиента без паспорта
        Client client = new Client();  // null как паспорт

        // Проверяем, что при вызове метода getPassport() выбрасывается исключение
        assertThrows(NullPointerException.class, () -> client.getPassport().getSeries());
    }
}
