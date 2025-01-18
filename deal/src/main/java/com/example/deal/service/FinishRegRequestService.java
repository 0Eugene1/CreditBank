package com.example.deal.service;

import com.example.deal.dto.CreditDto;
import com.example.deal.dto.FinishRegistrationRequestDto;
import com.example.deal.dto.ScoringDataDto;
import com.example.deal.entity.Credit;
import com.example.deal.entity.Statement;
import com.example.deal.enums.ApplicationStatus;
import com.example.deal.enums.ChangeType;
import com.example.deal.exception.StatementNotFoundException;
import com.example.deal.feignclient.CalculatorOffersClient;
import com.example.deal.json.StatusHistory;
import com.example.deal.mapper.CreditMapper;
import com.example.deal.mapper.ScoringDataMapper;
import com.example.deal.mapper.StatusHistoryMapper;
import com.example.deal.repository.CreditRepository;
import com.example.deal.repository.StatementRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Service
public class FinishRegRequestService {

    private final CreditRepository creditRepository;
    private final StatementRepository statementRepository;
    private final CreditMapper creditMapper;
    private final StatusHistoryMapper statusHistoryMapper;
    private final CalculatorOffersClient calculatorScoringClient;

    @Transactional
    public void finishRegistration(String statementId, FinishRegistrationRequestDto registrationRequest) {
        log.info("Starting finishRegistration for statementId: {}, with request: {}", statementId, registrationRequest);

        // 1. Найти Statement по ID
        Statement statement = statementRepository.findById(UUID.fromString(statementId))
                .orElseThrow(() -> new StatementNotFoundException("Statement not found"));

        // 2. Проверка данных клиента и создание ScoringDataDto
        if (statement.getClient() != null && statement.getClient().getPassport() == null) {
            log.error("Data is missing for : {} {}, Statement ID: {}",
                    statement.getClient().getFirstName(),
                    statement.getClient().getLastName(),
                    statement.getStatementId());
            throw new IllegalArgumentException("Passport data is missing for the client.");
        }

        // Используем маппер для создания объекта ScoringDataDto
        ScoringDataDto scoringData = ScoringDataMapper.toScoringDataDto(registrationRequest, statement);

        CreditDto creditDto = calculatorScoringClient.sendScoringData(scoringData);

        Credit credit = creditMapper.creditToEntity(creditDto);
        creditRepository.save(credit);

        log.info("Credit entity saved: {}", credit);

        updateStatusHistory(statement, ApplicationStatus.PREAPPROVAL);

        statement.setCredit(credit);
        log.info("Statement updated and saved with new credit: {}", statement);
    }

    private void updateStatusHistory(Statement statement, ApplicationStatus newStatus) {
        log.info("Updating status history for statement: {}", statement);

        // Получаем текущую историю статусов или создаем новую
        List<StatusHistory> statusList = statement.getStatusHistory() != null
                ? new ArrayList<>(statement.getStatusHistory())
                : new ArrayList<>();

        // Используем StatusHistoryMapper для создания нового статуса
        StatusHistory statusHistory = statusHistoryMapper.toEntity(newStatus, ChangeType.AUTOMATIC);

        // Добавляем новый статус
        statusList.add(statusHistory);

        // Обновляем статус и историю
        statement.setStatus(newStatus);
        statement.setStatusHistory(statusList);

        log.info("Status history updated for statement {}: {}", statement.getStatementId(), statusList);
    }
}