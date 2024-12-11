package com.example.deal.service;

import com.example.deal.dto.LoanOfferDto;
import com.example.deal.dto.StatementStatusHistoryDto;
import com.example.deal.entity.Statement;
import com.example.deal.enums.ApplicationStatus;
import com.example.deal.exception.StatementNotFoundException;
import com.example.deal.repository.StatementRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SelectOffersServiceTest {

    @Mock
    private StatementRepository statementRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private SelectOffersService selectOffersService;


    @Test
    void selectLoanOffer_shouldThrowStatementNotFoundException_whenStatementNotFound() {
        // Создаем мок для LoanOfferDto
        LoanOfferDto mockOffer = LoanOfferDto.builder()
                .statementId(UUID.randomUUID())
                .build();

        // Настроим mock для findById, чтобы возвращать пустой Optional
        when(statementRepository.findById(mockOffer.getStatementId())).thenReturn(Optional.empty());

        // Проверка, что метод выбрасывает StatementNotFoundException
        assertThrows(StatementNotFoundException.class, () -> selectOffersService.selectLoanOffer(mockOffer));
    }
}