package com.example.deal.service;

import com.example.deal.dto.EmailMessage;
import com.example.deal.dto.LoanOfferDto;
import com.example.deal.enums.ThemeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OfferService {

    private final KafkaProducerService kafkaProducerService;

    public void processOffer(LoanOfferDto offer) {
        log.info("Обработка оффера: {}", offer);

        // Формируем сообщение
        EmailMessage emailMessage = EmailMessage.builder()
                .address("client@example.com") // Адрес клиента
                .theme(ThemeEnum.SEND_DOCUMENTS)
                .statementId(offer.getStatementId())
                .text("Ваше предложение по кредиту создано.")
                .build();

        // Отправка события в Kafka с объектом EmailMessage
        kafkaProducerService.sendMessage("finish-registration", emailMessage);
    }
}
