package com.example.dossier.service;

import com.example.deal.dto.EmailMessage;
import com.example.deal.enums.ThemeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableKafka
public class KafkaConsumerService {

    private final EmailService emailService;

    @KafkaListener(topics = {
            "finish-registration",
            "create-documents",
            "send-documents",
            "send-ses",
            "credit-issued",
            "statement-denied"},
            groupId = "dossier-consumer-group")
    public void listenToTopics(EmailMessage message) {
        log.info("Received message: {}", message);

        // Проверяем, если тема "send-documents", то формируем файл
        if (message.getTheme() == ThemeEnum.SEND_DOCUMENTS) {
            generateDocumentFile(message);
        }

        // Отправляем email после выполнения нужных действий
        emailService.sendEmail(message);
    }

    private void generateDocumentFile(EmailMessage message) {
        // Логика для формирования файла для send-documents
        String documentText = message.getText();  // Текст документа
        String fileName = "document_" + message.getStatementId() + ".txt";  // Имя файла

        Path documentsDirectory = Path.of("documents");
        Path filePath = documentsDirectory.resolve(fileName);

        try {
            if (Files.notExists(documentsDirectory)) {
                Files.createDirectories(documentsDirectory);
                log.info("Created documents directory: {}", documentsDirectory);
            }

            Files.write(filePath, documentText.getBytes(), StandardOpenOption.CREATE);
            log.info("Document file created: {}", filePath);

            // Обновляем сообщение, чтобы включить путь к файлу в текст email
            message.setText(filePath.toString());

        } catch (Exception e) {
            log.error("Error while generating document file for statementId: {}", message.getStatementId(), e);
        }
    }
}
