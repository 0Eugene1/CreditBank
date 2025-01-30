package com.example.deal.service;

import com.example.deal.entity.Statement;
import com.example.deal.repository.StatementRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StatementService {

    private final StatementRepository repository;

    // Получение заявки по ID
    public Statement getStatementById(UUID statementId) {
        return repository.findById(statementId)
                .orElseThrow(() -> new EntityNotFoundException("Statement not found for ID: " + statementId));
    }

    // Получение всех заявок
    public List<Statement> getAllStatements() {
        return repository.findAll();
    }
}

