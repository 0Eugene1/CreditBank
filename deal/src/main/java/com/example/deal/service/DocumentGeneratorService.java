package com.example.deal.service;

import com.example.deal.entity.Client;
import com.example.deal.entity.Statement;
import com.example.deal.repository.ClientRepository;
import com.example.deal.repository.StatementRepository;
import com.example.deal.dto.EmailMessage;
import com.example.deal.enums.ThemeEnum;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentGeneratorService {

    private final StatementRepository statementRepository;
    private final KafkaProducerService kafkaProducerService;
    private final ClientRepository clientRepository;

    // Генерация текста для документа
    public String generateDocumentText(UUID statementId) {
        Statement statement = statementRepository.findById(statementId)
                .orElseThrow(() -> new EntityNotFoundException("Statement not found for ID: " + statementId));

        // Логика генерации текста документа
        return "Документ для statementId " + statementId + ", клиент: " + statement.getClient();
    }

    // Отправка сообщения в Kafka о создании документов
    public void notifyDocumentCreation(UUID statementId) {
        String message = "Документы создаются для statementId: " + statementId;

        Client client = clientRepository.findByStatements_StatementId(statementId)
                .orElseThrow(() -> new EntityNotFoundException("Client not found for statementId: " + statementId));

        EmailMessage createDocumentsMessage = EmailMessage.builder()
                .address(client.getEmail())
                .theme(ThemeEnum.CREATE_DOCUMENTS)
                .statementId(statementId)
                .text(message)
                .build();

        // Отправка сообщения в Kafka топик "create-documents"
        kafkaProducerService.sendMessage("create-documents", createDocumentsMessage);
    }

    // Отправка сообщения в Kafka о готовности документов
    public void sendDocuments(UUID statementId) {
        String documentText = generateDocumentText(statementId);

        Client client = clientRepository.findByStatements_StatementId(statementId)
                .orElseThrow(() -> new EntityNotFoundException("Client not found for statementId: " + statementId));

        // Создаем сообщение для топика "send-documents"
        EmailMessage sendDocumentsMessage = EmailMessage.builder()
                .address(client.getEmail())
                .theme(ThemeEnum.SEND_DOCUMENTS)
                .statementId(statementId)
                .text(documentText)
                .build();

        // Отправка сообщения в Kafka
        kafkaProducerService.sendMessage("send-documents", sendDocumentsMessage);
    }
}
