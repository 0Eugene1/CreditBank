package com.example.deal.service;

import com.example.deal.dto.CreditDto;
import com.example.deal.dto.FinishRegistrationRequestDto;
import com.example.deal.dto.FinishRegistrationRequestDto;
import com.example.deal.dto.PaymentScheduleElementDto;
import com.example.deal.dto.ScoringDataDto;
import com.example.deal.entity.Credit;
import com.example.deal.entity.Statement;
import com.example.deal.enums.ApplicationStatus;
import com.example.deal.enums.CreditStatus;
import com.example.deal.exception.StatementNotFoundException;
import com.example.deal.mapper.ScoringDataMapper;
import com.example.deal.mccalculator.CalculatorClient;
import com.example.deal.repository.CreditRepository;
import com.example.deal.repository.StatementRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final CalculatorClient calculateCredit;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public void finishRegistration(String statementId, FinishRegistrationRequestDto registrationRequest) {
        log.info("Starting finishRegistration for statementId: {}, with request: {}", statementId, registrationRequest);

        // 1. Найти Statement по ID
        Statement statement = statementRepository.findById(UUID.fromString(statementId))
                .orElseThrow(() -> new StatementNotFoundException("Statement not found"));

        // 2. Создать ScoringDataDto
        ScoringDataDto scoringData = getInformation(registrationRequest, statement);

        // 3. Отправить ScoringDataDto в кредитный конвейер и получить CreditDto
        CreditDto creditDto = calculateCredit.sendScoringData(scoringData);

        // 4. Создать сущность Credit и сохранить в базу
        Credit credit = createCreditFromDto(creditDto);

        // 5. Обновить статус Statement
        statement.setStatus(ApplicationStatus.PREAPPROVAL);
        updateStatusHistory(statement);

        // 6. Сохранить Credit и Statement
        statement.setCredit(credit);
        statementRepository.save(statement);
        log.info("Statement updated and saved with new credit: {}", statement);
    }


    public ScoringDataDto getInformation(FinishRegistrationRequestDto registrationRequest, Statement statement) {
        log.info("Creating ScoringDataDto from FinishRegistrationRequestDto and Statement.");

        if (statement.getClient() != null && statement.getClient().getPassport() == null) {
            log.error("Passport data missing for client: {} {}", statement.getClient().getFirstName(), statement.getClient().getLastName());
        }

        // Используем маппер для создания объекта ScoringDataDto
        ScoringDataDto scoringData = ScoringDataMapper.toScoringDataDto(registrationRequest, statement);

        log.debug("Generated ScoringDataDto: {}", scoringData);
        return scoringData;
    }

    public Credit createCreditFromDto(CreditDto creditDto) {
        log.info("Creating Credit entity from CreditDto: {}", creditDto);

        Credit credit = new Credit();
        credit.setAmount(creditDto.getAmount());
        credit.setPsk(creditDto.getPsk());
        credit.setRate(creditDto.getRate());
        credit.setInsuranceEnabled(creditDto.isInsuranceEnabled());
        credit.setSalaryClient(creditDto.isSalaryClient());
        // Десериализация paymentSchedule


        // Устанавливаем paymentSchedule напрямую
        credit.setPaymentSchedule(creditDto.getPaymentSchedule());

        credit.setMonthlyPayment(creditDto.getMonthlyPayment());
        credit.setTerm(creditDto.getTerm());
        credit.setCreditStatus(CreditStatus.CALCULATED);

        log.info("Creating Credit from DTO: {}", creditDto);
        return creditRepository.save(credit);
    }

    private void updateStatusHistory(Statement statement) {
        log.info("Updating status history for statement: {}", statement);

        // Получаем текущий список статусов (если существует)
        String currentHistory = statement.getStatusHistory();
        List<String> statusList = new ArrayList<>();

        // Если в текущей истории что-то есть, десериализуем ее
        if (currentHistory != null && !currentHistory.isEmpty()) {
            try {
                statusList = new ObjectMapper().readValue(currentHistory, new TypeReference<List<String>>() {
                });
            } catch (JsonProcessingException e) {
                log.error("Error parsing current status history", e);
            }
        }

        // Добавляем новый статус
        statusList.add(ApplicationStatus.PREAPPROVAL.name());

        // Преобразуем список в JSON строку и сохраняем
        try {
            String updatedHistory = new ObjectMapper().writeValueAsString(statusList);
            statement.setStatusHistory(updatedHistory);
            log.info("Updated status history for statement {}: {}", statement, updatedHistory);
        } catch (JsonProcessingException e) {
            log.error("Error serializing updated status history", e);
        }
    }
}
