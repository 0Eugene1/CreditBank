package com.example.deal.mapper;

import com.example.deal.dto.LoanStatementRequestDto;
import com.example.deal.entity.Client;
import com.example.deal.json.Passport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Slf4j
public class ClientMapper {

    public Client toEntity(LoanStatementRequestDto request) {

        Client client = new Client();
        client.setFirstName(request.getFirstName());
        client.setLastName(request.getLastName());
        client.setMiddleName(request.getMiddleName());
        client.setEmail(request.getEmail());
        client.setBirthDate(request.getBirthDate());

        // Создаем объект Passport
        Passport passport = new Passport();
        passport.setSeries(request.getPassportSeries());
        passport.setNumber(request.getPassportNumber());

        passport.setIssueDate(LocalDate.now()); // Пример значения по умолчанию
        passport.setIssueBranch("Не указано");  // Пример значения по умолчанию

        // Устанавливаем паспорт в клиента
        client.setPassport(passport);

        log.info("Создан паспорт: {}", passport);

        log.info("Создан клиент: {}", client);

        return client;
    }
}
