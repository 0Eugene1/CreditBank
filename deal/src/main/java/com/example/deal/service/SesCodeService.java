package com.example.deal.service;

import com.example.deal.dto.EmailMessage;
import com.example.deal.entity.Client;
import com.example.deal.entity.SesCode;
import com.example.deal.enums.ThemeEnum;
import com.example.deal.repository.ClientRepository;
import com.example.deal.repository.SesCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class SesCodeService {

    private final SesCodeRepository sesCodeRepository;
    private final KafkaProducerService kafkaProducerService;
    private final ClientRepository clientRepository;

    private static final long EXPIRATION_TIME = TimeUnit.MINUTES.toMillis(10); // Код истекает через 10 минут

    public String generateSesCode(UUID statementId) {
        // Генерация случайного кода
        String sesCode = String.format("%06d", (int) (Math.random() * 1000000));

        // Сохраняем код в БД
        SesCode sesCodeEntity = new SesCode();
        sesCodeEntity.setStatementId(statementId);
        sesCodeEntity.setSesCode(sesCode);
        sesCodeEntity.setTimestamp(System.currentTimeMillis());

        sesCodeRepository.save(sesCodeEntity);

        return sesCode;
    }

    public boolean validateSesCode(UUID statementId, String inputCode) {
        Optional<SesCode> sesCodeEntityOpt = sesCodeRepository.findByStatementId(statementId);

        if (sesCodeEntityOpt.isPresent()) {
            SesCode sesCodeEntity = sesCodeEntityOpt.get();
            log.info("Stored SES code: {}", sesCodeEntity.getSesCode());
            log.info("Input SES code: {}", inputCode);

            // Проверка срока действия кода
            if (System.currentTimeMillis() - sesCodeEntity.getTimestamp() > EXPIRATION_TIME) {
                sesCodeRepository.delete(sesCodeEntity); // Удаление истекшего кода
                sendStatementDeniedEvent(statementId, "Ses-code expired");
                return false;
            }

            // Сравнение кодов
            if (!sesCodeEntity.getSesCode().equals(inputCode)) {
                sendStatementDeniedEvent(statementId, "Invalid ses-code provided");
                return false;
            }

            return true; // Код верный и действителен
        } else {
            // Если код не найден, генерируем новый код и возвращаем false
            String generatedCode = generateSesCode(statementId);
            log.info("Новый SES код сгенерирован для statementId: {}. Новый код: {}", statementId, generatedCode);
            sendStatementDeniedEvent(statementId, "SES code not found. New code generated.");
            return false;
        }
    }


    private void sendStatementDeniedEvent(UUID statementId, String reason) {

        // Поиск email client на основе statementId
        Client client = clientRepository.findByStatements_StatementId(statementId)
                .orElseThrow(() -> new RuntimeException("Клиент не найден для statementId: " + statementId));

        // Создание EmailMessage с информацией об отказе
        EmailMessage deniedMessage = EmailMessage.builder()
                .address(client.getEmail())
                .theme(ThemeEnum.CREDIT_ISSUED)
                .statementId(statementId)
                .text("Отказ по заявке: " + reason)
                .build();

        kafkaProducerService.sendMessage("statement-denied", deniedMessage);
        log.info("Сообщение об отказе отправлено в Kafka для statementId: {}, причина: {}", statementId, reason);
    }
}
