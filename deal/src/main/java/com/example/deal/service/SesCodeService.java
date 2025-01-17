package com.example.deal.service;

import com.example.deal.dto.EmailMessage;
import com.example.deal.entity.Client;
import com.example.deal.entity.Statement;
import com.example.deal.enums.ThemeEnum;
import com.example.deal.repository.ClientRepository;
import com.example.deal.repository.StatementRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SesCodeService {

    private final KafkaProducerService kafkaProducerService;
    private final StatementRepository statementRepository;
    private final ClientRepository clientRepository;


    public String generateAndSendSesCode(UUID statementId) {
        log.info("Генерация и отправка SES-кода для statementId {}", statementId);

        Statement statement = statementRepository.findById(statementId)
                .orElseThrow(() -> new EntityNotFoundException("Statement not found for ID: " + statementId));
        String sesCode = generateSesCode();
        statement.setSesCode(sesCode);
        statementRepository.save(statement);

        Client client = clientRepository.findByStatements_StatementId(statementId)
                .orElseThrow(() -> new EntityNotFoundException("Client not found for statementId: " + statementId));

        // Отправка сообщения в Kafka
        EmailMessage sesMessage = EmailMessage.builder()
                .address(client.getEmail())
                .theme(ThemeEnum.SEND_SES)
                .statementId(statementId)
                .text("SES код " + sesCode + " отправлен для statementId: " + statementId)
                .build();
        kafkaProducerService.sendMessage("send-ses", sesMessage);
        log.info("SES-код отправлен клиенту с email: {}", client.getEmail());

        return sesCode;
    }

    public void validateSesCode(UUID statementId, String sesCode) {
        Statement statement = statementRepository.findById(statementId)
                .orElseThrow(() -> new EntityNotFoundException("Statement not found for ID: " + statementId));

        // Если коды не совпадают, выбрасываем исключение и отправляем сообщение в топик statement-denied
        if (!sesCode.equals(statement.getSesCode())) {

            Client client = statement.getClient();
            EmailMessage message = EmailMessage.builder()
                    .address(client.getEmail())
                    .theme(ThemeEnum.STATEMENT_DENIED)
                    .statementId(statementId)
                    .text("Код подписания не совпадает для заявка: " + statementId)
                    .build();

            // Отправляем сообщение в топик statement-denied
            kafkaProducerService.sendMessage("statement-denied", message);
            log.info("Сообщение о невалидном SES-коде отправлено в топик statement-denied для statementId {}", statementId);

            throw new IllegalArgumentException("Код подписания не совпадает!");
        }
    }


    private String generateSesCode() {
        int code = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(code);
    }
}
