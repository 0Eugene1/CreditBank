package com.example.deal.service;

import com.example.deal.dto.EmailMessage;
import com.example.deal.dto.LoanOfferDto;
import com.example.deal.entity.Client;
import com.example.deal.enums.ThemeEnum;
import com.example.deal.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService {

    private final SesCodeService sesCodeService;
    private final ClientRepository clientRepository;
    private final KafkaProducerService kafkaProducerService;

    public void sendDocuments(UUID statementId, LoanOfferDto loanOffer) {
        // Генерация содержимого документа
        String documentContent = generateDocumentContent(statementId, loanOffer);

        // Создание файла
        File documentFile = createDocumentFile(documentContent, statementId);

        // Поиск email client на основе statementId
        Client client = clientRepository.findByStatements_StatementId(statementId)
                .orElseThrow(() -> new RuntimeException("Клиент не найден для statementId: " + statementId));

        // Генерация уникального ses-code и сохранение его в бд
        String sesCode = sesCodeService.generateSesCode(statementId);

        try {
            // Отправка письма
            EmailMessage emailMessage = EmailMessage.builder()
                    .address(client.getEmail()) // Реальный адрес клиента
                    .theme(ThemeEnum.SEND_DOCUMENTS)
                    .statementId(statementId)
                    .text("Документы по вашему запросу отправлены: " + sesCode)
                    .build();

            kafkaProducerService.sendMessage("send-documents", emailMessage);
            log.info("Документы отправлены для statementId: {}", statementId);
        } finally {
            // Удаление временного файла после отправки
            if (documentFile.exists()) {
                documentFile.delete();
            }
        }
    }

    private String generateDocumentContent(UUID statementId, LoanOfferDto loanOffer) {
        return String.format("""
            Кредитный договор
            -------------------
            Идентификатор заявления: %s
            Запрошенная сумма: %s
            Общая сумма: %s
            Срок кредита: %s месяцев
            Ежемесячный платёж: %s
            Ставка: %s%%
            Страховка: %s
            Зарплатный клиент: %s
            """,
                statementId,
                loanOffer.getRequestedAmount(),
                loanOffer.getTotalAmount(),
                loanOffer.getTerm(),
                loanOffer.getMonthlyPayment(),
                loanOffer.getRate(),
                loanOffer.getInsuranceEnabled() ? "Да" : "Нет",
                loanOffer.getSalaryClient() ? "Да" : "Нет");
    }
    private File createDocumentFile(String content, UUID statementId) {
        try {
            File tempFile = File.createTempFile("loan-document-" + statementId, ".txt");
            try (FileWriter writer = new FileWriter(tempFile)) {
                writer.write(content);
            }
            return tempFile;
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при создании документа: " + e.getMessage(), e);
        }
    }

    public void signDocuments(UUID statementId) {
        log.info("Документы для statementId {} готовы к подписанию.", statementId);

        // Поиск email client на основе statementId
        Client client = clientRepository.findByStatements_StatementId(statementId)
                .orElseThrow(() -> new RuntimeException("Клиент не найден для statementId: " + statementId));

        EmailMessage message = EmailMessage.builder()
                .address(client.getEmail())
                .theme(ThemeEnum.SIGN_DOCUMENTS)
                .statementId(statementId)
                .text("Пожалуйста, подпишите ваши документы по ссылке.")
                .build();

        kafkaProducerService.sendMessage("send-ses", message);
        log.info("Сообщение о подписании документов отправлено в Kafka для statementId: {}", statementId);
    }

    public void confirmCode(UUID statementId) {
        log.info("Код подтверждения документов для statementId {} проверен.", statementId);

        // Поиск email client на основе statementId
        Client client = clientRepository.findByStatements_StatementId(statementId)
                .orElseThrow(() -> new RuntimeException("Клиент не найден для statementId: " + statementId));

        EmailMessage message = EmailMessage.builder()
                .address(client.getEmail())
                .theme(ThemeEnum.SEND_DOCUMENTS)
                .statementId(statementId)
                .text("Ваши документы успешно подписаны!")
                .build();

        kafkaProducerService.sendMessage("credit-issued", message);
        log.info("Уведомление о завершении подписания документов отправлено в Kafka для statementId: {}", statementId);
    }
}
