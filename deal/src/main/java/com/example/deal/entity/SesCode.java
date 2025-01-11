package com.example.deal.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
public class SesCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Используем генерацию ID
    private Long id;
    private UUID statementId;
    private String sesCode;
    private Long timestamp;

}

