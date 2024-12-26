package com.example.deal.json;

import com.example.deal.enums.EmploymentPosition;
import com.example.deal.enums.EmploymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Employment {

    @Id
    @GeneratedValue
    private UUID employmentUud;

    @Enumerated(EnumType.STRING)
    private EmploymentStatus status;                 // Статус занятости

    private String employerInn;            // ИНН работодателя
    private BigDecimal salary;                 // Зарплата

    @Enumerated(EnumType.STRING)
    private EmploymentPosition position;               // Должность

    private int workExperienceTotal;       // Общий стаж работы
    private int workExperienceCurrent;     // Стаж работы на текущем месте
}
