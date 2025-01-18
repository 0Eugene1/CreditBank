package com.example.deal.service;

import com.example.deal.entity.Statement;
import com.example.deal.enums.ApplicationStatus;
import com.example.deal.enums.ChangeType;
import com.example.deal.json.StatusHistory;
import com.example.deal.repository.StatementRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationStatusService {

    private final StatementRepository statementRepository;

    public void updateStatus(UUID statementId, ApplicationStatus applicationStatus, ChangeType changeType) {
        Statement statement = statementRepository.findById(statementId)
                .orElseThrow(() -> new EntityNotFoundException("Statement not found for ID: " + statementId));

        statement.setStatus(applicationStatus);

        StatusHistory statusHistory = new StatusHistory();
        statusHistory.setStatus(applicationStatus);
        statusHistory.setTime(LocalDateTime.now());
        statusHistory.setChangeType(changeType);

        List<StatusHistory> history = statement.getStatusHistory();
        history.add(statusHistory);
        statement.setStatusHistory(history);

        statementRepository.save(statement);
    }
}

