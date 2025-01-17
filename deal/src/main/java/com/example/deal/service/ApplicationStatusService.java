package com.example.deal.service;

import com.example.deal.entity.Statement;
import com.example.deal.enums.ApplicationStatus;
import com.example.deal.repository.StatementRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationStatusService {

    private final StatementRepository statementRepository;

    public void updateStatusToCreditIssued(UUID statementId) {
        log.info("Обновление статуса на CREDIT_ISSUED для statementId {}", statementId);
        Statement statement = statementRepository.findById(statementId)
                .orElseThrow(() -> new EntityNotFoundException("Statement not found for ID: " + statementId));

        statement.setStatus(ApplicationStatus.CREDIT_ISSUED);
        statementRepository.save(statement);
    }

    public void updateStatusToClientDenied(UUID statementId) {
        log.info("Обновление статуса на CLIENT_DENIED для statementId {}", statementId);
        Statement statement = statementRepository.findById(statementId)
                .orElseThrow(() -> new EntityNotFoundException("Statement not found for ID: " + statementId));

        statement.setStatus(ApplicationStatus.CLIENT_DENIED);
        statementRepository.save(statement);
    }

    public void updateStatusToFinishRegistration(UUID statementId) {
        log.info("Обновление статуса на DOCUMENT_SIGNED для statementId {}", statementId);
        Statement statement = statementRepository.findById(statementId)
                .orElseThrow(() -> new EntityNotFoundException("Statement not found for ID: " + statementId));

        statement.setStatus(ApplicationStatus.DOCUMENT_SIGNED);
        statementRepository.save(statement);
    }
}

