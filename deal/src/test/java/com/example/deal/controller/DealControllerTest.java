package com.example.deal.controller;

import com.example.deal.dto.EmploymentDto;
import com.example.deal.dto.FinishRegistrationRequestDto;
import com.example.deal.dto.LoanOfferDto;
import com.example.deal.dto.LoanStatementRequestDto;
import com.example.deal.enums.EmploymentPosition;
import com.example.deal.enums.EmploymentStatus;
import com.example.deal.enums.Gender;
import com.example.deal.enums.MaritalStatus;
import com.example.deal.service.FinishRegRequestService;
import com.example.deal.service.LoanOfferService;
import com.example.deal.service.SelectOfferService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class DealControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LoanOfferService loanOfferService;

    @MockBean
    private SelectOfferService selectOffersService;

    @MockBean
    private FinishRegRequestService finishRegRequestService;

    private LoanStatementRequestDto requestDto;
    private LoanOfferDto offerDto;
    private FinishRegistrationRequestDto registrationDto;

    @BeforeEach
    void setUp() {
        // Инициализация общих данных для тестов
        requestDto = LoanStatementRequestDto.builder()
                .amount(BigDecimal.valueOf(100000))
                .term(12)
                .firstName("Иван")
                .lastName("Иванов")
                .middleName("Иванович")
                .email("ivanov@example.com")
                .birthDate(LocalDate.of(1990, 1, 1))
                .passportSeries("1234")
                .passportNumber("567890")
                .build();

        offerDto = LoanOfferDto.builder()
                .statementId(UUID.randomUUID())
                .requestedAmount(BigDecimal.valueOf(100000))
                .totalAmount(BigDecimal.valueOf(120000))
                .term(12)
                .monthlyPayment(BigDecimal.valueOf(10000))
                .rate(BigDecimal.valueOf(12.5))
                .insuranceEnabled(true)
                .salaryClient(false)
                .build();

        registrationDto = FinishRegistrationRequestDto.builder()
                .gender(Gender.MALE)
                .maritalStatus(MaritalStatus.SINGLE)
                .dependentAmount(0)
                .passportIssueDate(LocalDate.of(2010, 5, 15))
                .passportIssueBranch("123456")
                .employment(EmploymentDto.builder()
                        .employmentStatus(EmploymentStatus.EMPLOYED)
                        .employerInn("1234567890")
                        .salary(BigDecimal.valueOf(50000))
                        .position(EmploymentPosition.WORKER)
                        .workExperienceTotal(5)
                        .workExperienceCurrent(2)
                        .build())
                .accountNumber("40817810099910004312")
                .build();
    }

    @Test
    void calculateLoanOffers_validRequest_returnsLoanOffers() throws Exception {
        Mockito.when(loanOfferService.createClientFromRequest(any()))
                .thenReturn(List.of(offerDto));

        mockMvc.perform(post("/deal/statement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].statementId").isNotEmpty())
                .andExpect(jsonPath("$[0].requestedAmount").value(100000))
                .andExpect(jsonPath("$[0].rate").value(12.5));
    }

    @Test
    void finishRegistration_validRequest_returnsOk() throws Exception {
        mockMvc.perform(post("/deal/calculate/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isOk());

        Mockito.verify(finishRegRequestService).finishRegistration(Mockito.eq("123"), Mockito.any());
    }

}
