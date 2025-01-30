package com.example.deal.service;

import com.example.deal.dto.EmailMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, EmailMessage> kafkaTemplate;

    public void sendMessage(String topic, EmailMessage message) {
        log.info("Отправка сообщения в топик {}: {}", topic, message);
        kafkaTemplate.send(topic, message);
    }
}
