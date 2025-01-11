package com.example.deal.repository;

import com.example.deal.entity.SesCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SesCodeRepository extends JpaRepository<SesCode, Long> {
    Optional<SesCode> findByStatementId(UUID statementId);
}

