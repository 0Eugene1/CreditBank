package com.example.deal.service;

import com.example.deal.dto.EmailMessage;
import com.example.deal.dto.LoanOfferDto;
import com.example.deal.entity.Statement;
import com.example.deal.enums.ApplicationStatus;
import com.example.deal.enums.ChangeType;
import com.example.deal.enums.ThemeEnum;
import com.example.deal.exception.StatementNotFoundException;
import com.example.deal.json.StatusHistory;
import com.example.deal.mapper.StatusHistoryMapper;
import com.example.deal.repository.StatementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class SelectOfferService {

    private final StatementRepository statementRepository;
    private final StatusHistoryMapper statusHistoryMapper;
    private final KafkaProducerService kafkaProducerService;


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
        statement.setAppliedOffer(offer);

        // Сохраняем обновленную заявку
        statementRepository.save(statement);

        log.info("Loan offer selected and statement updated: {}", statement);

    }

    public void processOffer(LoanOfferDto offer) {
        log.info("Обработка оффера: {}", offer);

        // Формируем сообщение
        EmailMessage emailMessage = EmailMessage.builder()
                .address("client@example.com") // Адрес клиента
                .theme(ThemeEnum.SIGN_DOCUMENTS)
                .statementId(offer.getStatementId())
                .text("Ваше предложение по кредиту создано.")
                .build();

        // Отправка события в Kafka с объектом EmailMessage
        kafkaProducerService.sendMessage("offer-created", emailMessage);
    }
}

