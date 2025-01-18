package com.example.deal.service;

import com.example.deal.dto.EmailMessage;
import com.example.deal.entity.Client;
import com.example.deal.entity.Statement;
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
public class DocumentGeneratorService {

    private final StatementRepository statementRepository;
    private final KafkaProducerService kafkaProducerService;
    private final ClientRepository clientRepository;

    // Получение Statement и связанного Client
    private Client getStatementAndClient(UUID statementId) {

        Statement statement = statementRepository.findById(statementId)
                .orElseThrow(() -> {
                    return new EntityNotFoundException("Statement not found for ID: " + statementId);
                });

        return clientRepository.findByStatements_StatementId(statementId)
                .orElseThrow(() -> {
                    log.error("Client not found for statementId: {}", statementId);
                    return new EntityNotFoundException("Client not found for statementId: " + statementId);
                });
    }

    // Генерация текста для документа
    public String generateDocumentText(UUID statementId) {

        Statement statement = statementRepository.findById(statementId)
                .orElseThrow(() -> {
                    log.error("Statement not found for ID: {}", statementId);
                    return new EntityNotFoundException("Statement not found for ID: " + statementId);
                });

        return "Документ для statementId " + statementId + ", клиент: " + statement.getClient();
    }

    // Отправка сообщения в Kafka о создании документов
    public void notifyDocumentCreation(UUID statementId) {

        Client client = getStatementAndClient(statementId);
        String message = "Документы создаются для statementId: " + statementId;

        EmailMessage createDocumentsMessage = EmailMessage.builder()
                .address(client.getEmail())
                .theme(ThemeEnum.CREATE_DOCUMENTS)
                .statementId(statementId)
                .text(message)
                .build();

        kafkaProducerService.sendMessage("create-documents", createDocumentsMessage);
        log.info("Document creation notification sent for statementId: {}", statementId);
    }

    // Отправка сообщения в Kafka о готовности документов
    public void sendDocuments(UUID statementId) {

        Client client = getStatementAndClient(statementId);
        String documentText = generateDocumentText(statementId);

        EmailMessage sendDocumentsMessage = EmailMessage.builder()
                .address(client.getEmail())
                .theme(ThemeEnum.SEND_DOCUMENTS)
                .statementId(statementId)
                .text(documentText)
                .build();

        kafkaProducerService.sendMessage("send-documents", sendDocumentsMessage);
        log.info("Documents sent for statementId: {}", statementId);
    }
}

