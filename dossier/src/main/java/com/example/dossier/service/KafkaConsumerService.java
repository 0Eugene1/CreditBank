package com.example.dossier.service;


import com.example.dossier.dto.EmailMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

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
        emailService.sendEmail(message);
    }

}
