package com.example.deal.service;

import com.example.deal.dto.EmailMessage;
import com.example.deal.entity.Statement;
import com.example.deal.enums.ApplicationStatus;
import com.example.deal.enums.ChangeType;
import com.example.deal.enums.ThemeEnum;
import com.example.deal.repository.StatementRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService {

    private final DocumentGeneratorService documentGeneratorService;
    private final SesCodeService sesCodeService;
    private final ApplicationStatusService applicationStatusService;
    private final KafkaProducerService kafkaProducerService;
    private final StatementRepository statementRepository;

    public void sendDocuments(UUID statementId) {
        log.info("Отправка документов для statementId {}", statementId);
        documentGeneratorService.notifyDocumentCreation(statementId);
        documentGeneratorService.sendDocuments(statementId);
    }

    public String generateAndSendSesCode(UUID statementId) {
        log.info("Генерация и отправка SES-кода для statementId {}", statementId);

        Statement statement = statementRepository.findById(statementId)
                .orElseThrow(() -> new EntityNotFoundException("Statement not found for ID: " + statementId));

        statement.setSignDate(LocalDateTime.now());
        statementRepository.save(statement);

        return sesCodeService.generateAndSendSesCode(statement);
    }

    public void validateSesCodeAndIssueCredit(UUID statementId, String sesCode) {
        log.info("Проверка SES-кода и выдача кредита для statementId {}", statementId);

        Statement statement = statementRepository.findById(statementId)
                .orElseThrow(() -> new EntityNotFoundException("Statement not found for ID: " + statementId));

        sesCodeService.validateSesCode(statement, sesCode);

        applicationStatusService.updateStatus(statementId, ApplicationStatus.CREDIT_ISSUED, ChangeType.AUTOMATIC);

        EmailMessage message = EmailMessage.builder()
                .address(statement.getClient().getEmail())
                .theme(ThemeEnum.CREDIT_ISSUED)
                .statementId(statementId)
                .text("Кредит выдан для statementId: " + statementId)
                .build();

        kafkaProducerService.sendMessage("credit-issued", message);
        log.info("Сообщение о выдаче кредита отправлено в топик credit-issued для statementId {}", statementId);
    }
}

