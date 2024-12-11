package com.example.deal.service;

import com.example.deal.dto.LoanOfferDto;
import com.example.deal.dto.StatementStatusHistoryDto;
import com.example.deal.entity.Statement;
import com.example.deal.enums.ApplicationStatus;
import com.example.deal.enums.ChangeType;
import com.example.deal.exception.StatementNotFoundException;
import com.example.deal.repository.StatementRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class SelectOffersService {

    private final StatementRepository statementRepository;
    private final ObjectMapper objectMapper;


    public void selectLoanOffer(LoanOfferDto offer) {
        log.info("Selecting loan offer: {}", offer);

        Statement statement = statementRepository.findById(offer.getStatementId())
                .orElseThrow(() -> new StatementNotFoundException("Statement не найден"));

        // Обновляем статус заявки
        ApplicationStatus newStatus = ApplicationStatus.DOCUMENT_CREATED;
        statement.setStatus(newStatus);

        // Добавляем историю статусов в поле status_history
        List<StatementStatusHistoryDto> historyList = statement.getStatusHistory() == null
                ? new ArrayList<>()
                : getStatusHistory(statement.getStatusHistory());


        historyList.add(createStatusHistoryEntry(newStatus));

        // Сохраняем обновленную историю статусов
        statement.setStatusHistory(convertStatusHistoryToJson(historyList));

        // Устанавливаем выбранное предложение в поле appliedOffer
        statement.setAppliedOffer(convertLoanOfferToJson(offer));

        // Сохраняем обновленную заявку
        statementRepository.save(statement);

        log.info("Loan offer selected and statement updated: {}", statement);

    }

    // Создание новой записи истории статусов
    private StatementStatusHistoryDto createStatusHistoryEntry(ApplicationStatus status) {

        return StatementStatusHistoryDto.builder()
                .status(status)
                .time(LocalDateTime.now())
                .changeType(ChangeType.AUTOMATIC)
                .build();
    }

    // Метод для преобразования истории статусов в JSON
    private String convertStatusHistoryToJson(List<StatementStatusHistoryDto> statusHistory) {
        // Преобразуем историю в JSON чтобы сохранить объект LoanOfferDto в поле appliedOffer
        try {
            return objectMapper.writeValueAsString(statusHistory);
        } catch (JsonProcessingException e) {
            log.error("Error serializing status history: {}", statusHistory, e);
            throw new RuntimeException("Ошибка при сериализации истории статусов", e);
        }
    }

    // Метод для преобразования LoanOfferDto в JSON
    private String convertLoanOfferToJson(LoanOfferDto loanOfferDto) {
        // Преобразуем предложение в JSON
        try {
            return objectMapper.writeValueAsString(loanOfferDto);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize object: {}", loanOfferDto, e);
            throw new RuntimeException("Ошибка при сериализации кредитного предложения", e);
        }
    }

    // Метод для получения истории статусов из строки JSON
    private List<StatementStatusHistoryDto> getStatusHistory(String json) {
        // Преобразуем строку JSON в список объектов StatementStatusHistoryDto
        try {
            return objectMapper.readValue(json, new TypeReference<>(){});
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize status history JSON: {}", json, e);
            throw new RuntimeException("Ошибка при десериализации истории статусов", e);
        }
    }

}

