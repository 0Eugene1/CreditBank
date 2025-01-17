package com.example.deal.service;

import com.example.deal.dto.EmailMessage;
import com.example.deal.entity.Client;
import com.example.deal.entity.Statement;
import com.example.deal.enums.ApplicationStatus;
import com.example.deal.enums.ThemeEnum;
import com.example.deal.repository.ClientRepository;
import com.example.deal.repository.StatementRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService {

    private final DocumentGeneratorService documentGeneratorService;
    private final SesCodeService sesCodeService;
    private final ApplicationStatusService applicationStatusService;
    private final KafkaProducerService kafkaProducerService;
    private final ClientRepository clientRepository;
    private final StatementRepository statementRepository;

    // Получение клиента по statementId
    private Client getClientByStatementId(UUID statementId) {
        return clientRepository.findByStatements_StatementId(statementId)
                .orElseThrow(() -> new EntityNotFoundException("Client not found for statementId: " + statementId));
    }

    // Отправка документов
    public void sendDocuments(UUID statementId) {
        log.info("Отправка документов для statementId {}", statementId);

        // Сообщение о создании документов
        documentGeneratorService.notifyDocumentCreation(statementId);

        // Генерация и отправка документов
        documentGeneratorService.sendDocuments(statementId);
    }

    // Генерация и отправка SES-кода
    public String generateAndSendSesCode(UUID statementId) {
        log.info("Генерация и отправка SES-кода для statementId {}", statementId);

        // Генерация и отправка SES-кода
        return sesCodeService.generateAndSendSesCode(statementId);
    }

    public void validateAndCompleteSigning(UUID statementId, String sesCode) {
        log.info("Проверка SES-кода для statementId {}", statementId);

        // Получение Statement по ID
        Statement statement = statementRepository.findById(statementId)
                .orElseThrow(() -> new EntityNotFoundException("Statement not found for ID: " + statementId));

        String storedSesCode = statement.getSesCode();

        // Обновление статуса на CREDIT_ISSUED
        applicationStatusService.updateStatus(statementId, ApplicationStatus.CREDIT_ISSUED);

        Client client = getClientByStatementId(statementId);

        // Создаем сообщение для топика credit-issued
        EmailMessage message = EmailMessage.builder()
                .address(client.getEmail())
                .theme(ThemeEnum.CREDIT_ISSUED)
                .statementId(statementId)
                .text("Кредит выдан для statementId: " + statementId)
                .build();

        // Отправка сообщения в топик credit-issued
        kafkaProducerService.sendMessage("credit-issued", message);
        log.info("Сообщение о выдаче кредита отправлено в топик credit-issued для statementId {}", statementId);
    }
}
