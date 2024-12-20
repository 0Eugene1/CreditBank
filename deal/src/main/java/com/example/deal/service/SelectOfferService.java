package com.example.deal.service;

import com.example.deal.dto.LoanOfferDto;
import com.example.deal.entity.Statement;
import com.example.deal.enums.ApplicationStatus;
import com.example.deal.enums.ChangeType;
import com.example.deal.exception.StatementNotFoundException;
import com.example.deal.json.StatusHistory;
import com.example.deal.mapper.StatusHistoryMapper;
import com.example.deal.repository.StatementRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class SelectOfferService {

    private final StatementRepository statementRepository;
    private final ObjectMapper objectMapper;
    private final StatusHistoryMapper statusHistoryMapper;


    public void selectLoanOffer(LoanOfferDto offer) {
        log.info("Selecting loan offer: {}", offer);

        Statement statement = statementRepository.findById(offer.getStatementId())
                .orElseThrow(() -> new StatementNotFoundException("Statement не найден"));

        // Обновляем статус заявки
        ApplicationStatus newStatus = ApplicationStatus.DOCUMENT_CREATED;
        statement.setStatus(newStatus);

        // Добавляем новый статус в историю
        List<StatusHistory> historyList = statement.getStatusHistory();
        if (historyList == null) {
            historyList = new ArrayList<>();
        }

        // Используем маппер для создания объекта StatusHistory
        StatusHistory newStatusHistory = statusHistoryMapper.toEntity(newStatus, ChangeType.AUTOMATIC);
        historyList.add(newStatusHistory);

        // Обновляем поле statusHistory
        statement.setStatusHistory(historyList);

        // Устанавливаем выбранное предложение в поле appliedOffer
        statement.setAppliedOffer(convertLoanOfferToJson(offer));


        // Сохраняем обновленную заявку
        statementRepository.save(statement);

        log.info("Loan offer selected and statement updated: {}", statement);

    }
    private String convertLoanOfferToJson(LoanOfferDto offer) {
        try {
            return objectMapper.writeValueAsString(offer);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize LoanOfferDto: {}", offer, e);
            throw new SerializationException("Ошибка при сериализации объекта", e);
        }
//
//    private List<StatementStatusHistoryDto> getStatusHistory(String json) {
//        try {
//            CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, StatementStatusHistoryDto.class);
//            return objectMapper.readValue(json, collectionType);
//        } catch (JsonProcessingException e) {
//            log.error("Failed to deserialize status history JSON: {}", json, e);
//            throw new RuntimeException("Ошибка при десериализации истории статусов", e);
//        }
//    }
    }
}

