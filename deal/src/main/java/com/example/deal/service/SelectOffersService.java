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
import com.fasterxml.jackson.databind.type.CollectionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationException;
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

    private String convertObjectToJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize object: {}", object, e);
            throw new SerializationException("Ошибка при сериализации объекта", e);
        }
    }

    // Преобразование истории статусов в JSON
    private String convertStatusHistoryToJson(List<StatementStatusHistoryDto> historyList) {
        return convertObjectToJson(historyList);
    }

    // Преобразование предложения в JSON
    private String convertLoanOfferToJson(LoanOfferDto offer) {
        return convertObjectToJson(offer);
    }

    private List<StatementStatusHistoryDto> getStatusHistory(String json) {
        try {
            CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, StatementStatusHistoryDto.class);
            return objectMapper.readValue(json, collectionType);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize status history JSON: {}", json, e);
            throw new RuntimeException("Ошибка при десериализации истории статусов", e);
        }
    }
}

