package com.example.deal.service;

import com.example.deal.dto.EmailMessage;
import com.example.deal.entity.Statement;
import com.example.deal.enums.ApplicationStatus;
import com.example.deal.enums.ChangeType;
import com.example.deal.enums.ThemeEnum;
import com.example.deal.repository.StatementRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SesCodeService {

    private final KafkaProducerService kafkaProducerService;
    private final StatementRepository statementRepository;
    private final ApplicationStatusService applicationStatusService;

    public String generateAndSendSesCode(Statement statement) {
        log.info("Генерация и отправка SES-кода для statementId {}", statement.getStatementId());

        // Генерация и сохранение SES-кода
        String sesCode = generateSesCode();
        statement.setSesCode(sesCode);
        statementRepository.save(statement);

        // Отправка сообщения в Kafka
        EmailMessage sesMessage = EmailMessage.builder()
                .address(statement.getClient().getEmail())
                .theme(ThemeEnum.SEND_SES)
                .statementId(statement.getStatementId())
                .text("SES код " + sesCode + " отправлен для statementId: " + statement.getStatementId())
                .build();

        applicationStatusService.updateStatus(statement.getStatementId(), ApplicationStatus.APPROVED, ChangeType.AUTOMATIC);
        kafkaProducerService.sendMessage("send-ses", sesMessage);
        log.info("SES-код отправлен клиенту с email: {}", statement.getClient().getEmail());

        return sesCode;
    }

    public void validateSesCode(Statement statement, String sesCode) {
        log.info("Валидация SES-кода для statementId {}", statement.getStatementId());

        if (!sesCode.equals(statement.getSesCode())) {
            log.warn("Код SES не совпадает для statementId {}", statement.getStatementId());

            EmailMessage message = EmailMessage.builder()
                    .address(statement.getClient().getEmail())
                    .theme(ThemeEnum.STATEMENT_DENIED)
                    .statementId(statement.getStatementId())
                    .text("Код подписания не совпадает для заявка: " + statement.getStatementId())
                    .build();

            applicationStatusService.updateStatus(statement.getStatementId(), ApplicationStatus.CLIENT_DENIED, ChangeType.AUTOMATIC);
            kafkaProducerService.sendMessage("statement-denied", message);

            throw new IllegalArgumentException("Код подписания не совпадает!");
        }
    }

    private String generateSesCode() {
        int code = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(code);
    }
}
