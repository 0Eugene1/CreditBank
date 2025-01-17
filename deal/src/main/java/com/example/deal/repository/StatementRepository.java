package com.example.deal.repository;

import com.example.deal.entity.Statement;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StatementRepository extends JpaRepository<Statement, UUID> {
    Optional<Statement> findById(@NotNull UUID statementId);
    Optional<Statement> findBySesCode(String sesCode);
}
