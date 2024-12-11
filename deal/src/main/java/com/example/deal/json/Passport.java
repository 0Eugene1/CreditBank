package com.example.deal.json;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Passport {
    @Id
    @GeneratedValue
    private UUID passportUuid;
    @NotEmpty(message = "Серия паспорта не может быть пустой")
    private String series;
    @NotEmpty(message = "Номер паспорта не может быть пустым")
    private String number;
    @NotEmpty(message = "Отделение выдачи паспорта не может быть пустым")
    private String issueBranch;
    @NotNull(message = "Дата выдачи паспорта не может быть пустой")
    private LocalDate issueDate;

}

