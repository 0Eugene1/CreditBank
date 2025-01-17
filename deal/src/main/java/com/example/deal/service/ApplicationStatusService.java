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

    public void updateStatus(UUID statementId, ApplicationStatus applicationStatus) {
        log.info("Обновление статуса на {} для statementId {}", applicationStatus, statementId);

        Statement statement = statementRepository.findById(statementId)
                .orElseThrow(() -> new EntityNotFoundException("Statement not found for ID: " + statementId));

        statement.setStatus(applicationStatus);
        statementRepository.save(statement);
    }
}

