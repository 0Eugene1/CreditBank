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

        // 3. Отправить ScoringDataDto в кредитный конвейер и получить CreditDto
        CreditDto creditDto = calculatorScoringClient.sendScoringData(scoringData);  // Прямой вызов клиента

        // 4. Создать сущность Credit и сохранить в базу
        Credit credit = creditMapper.creditToEntity(creditDto);
        Credit savedCredit = creditRepository.save(credit);

        log.info("Credit entity saved: {}", savedCredit);

        // 5. Обновить статус Statement
        statement.setStatus(ApplicationStatus.PREAPPROVAL);
        updateStatusHistory(statement);

        // 6. Сохранить Credit и Statement
        statement.setCredit(savedCredit);
        statementRepository.save(statement);
        log.info("Statement updated and saved with new credit: {}", statement);
    }

    private void updateStatusHistory(Statement statement) {
        log.info("Updating status history for statement: {}", statement);

        // Получаем текущую историю статусов (если существует)
        List<StatusHistory> statusList = statement.getStatusHistory();
        if (statusList == null) {
            statusList = new ArrayList<>();
        }

        // Используем StatusHistoryMapper для создания нового статуса
        StatusHistory newStatus = statusHistoryMapper.toEntity(ApplicationStatus.PREAPPROVAL, ChangeType.AUTOMATIC);

        // Добавляем новый статус в историю
        statusList.add(newStatus);

        // Обновляем history и статус заявки
        statement.setStatusHistory(statusList); // Обновляем историю статусов
        statement.setStatus(ApplicationStatus.PREAPPROVAL); // Обновляем статус заявки

        // Сохраняем обновленную заявку
        statementRepository.save(statement);

        log.info("Updated status history and status for statement {}: {}", statement, statusList);
    }
}