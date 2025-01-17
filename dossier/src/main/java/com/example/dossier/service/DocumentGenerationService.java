package com.example.dossier.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Service
@Slf4j
public class DocumentGenerationService {

    // Метод для формирования файла документа
    public Path generateDocumentFile(String documentText, String statementId) throws IOException {
        // Используем временную директорию
        Path tempDirectory = Files.createTempDirectory("documents_");

        String fileName = "document_" + statementId + ".txt";  // Имя файла
        Path filePath = tempDirectory.resolve(fileName);  // Путь к файлу

        // Записываем содержимое в файл
        Files.write(filePath, documentText.getBytes(), StandardOpenOption.CREATE);
        log.info("Document file created at: {}", filePath);

        return filePath;  // Возвращаем путь к созданному временному файлу
    }
}
