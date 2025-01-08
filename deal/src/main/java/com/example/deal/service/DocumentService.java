package com.example.deal.service;

import com.example.deal.dto.EmailMessage;
import com.example.deal.enums.ThemeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService {

    private final KafkaProducerService kafkaProducerService;

    public void sendDocuments(UUID statementId) {
        // Логика отправки документов, например, генерация данных
        log.info("Отправка документов для statementId: {}", statementId);

        // Формируем сообщение
        EmailMessage message = EmailMessage.builder()
                .address("client@example.com") // Реальный адрес клиента
                .theme(ThemeEnum.SEND_DOCUMENTS) // Перечисление для темы письма
                .statementId(statementId)
                .text("Документы по вашему запросу отправлены.")
                .build();

        // Отправка в Kafka
        kafkaProducerService.sendMessage("send-documents", message);
        log.info("Документы для statementId {} отправлены в Kafka", statementId);
    }

    public void signDocuments(UUID statementId) {
        log.info("Документы для statementId {} готовы к подписанию.", statementId);

        EmailMessage message = EmailMessage.builder()
                .address("client@example.com")
                .theme(ThemeEnum.SIGN_DOCUMENTS)
                .statementId(statementId)
                .text("Пожалуйста, подпишите ваши документы по ссылке.")
                .build();


        kafkaProducerService.sendMessage("send-ses", message);
        log.info("Сообщение о подписании документов отправлено в Kafka для statementId: {}", statementId);
    }

    public void confirmCode(UUID statementId) {
        log.info("Код подтверждения документов для statementId {} проверен.", statementId);

        EmailMessage message = EmailMessage.builder()
                .address("client@example.com")
                .theme(ThemeEnum.CREDIT_ISSUED)
                .statementId(statementId)
                .text("Ваши документы успешно подписаны!")
                .build();

        kafkaProducerService.sendMessage("credit-issued", message);
        log.info("Уведомление о завершении подписания документов отправлено в Kafka для statementId: {}", statementId);
    }

}
