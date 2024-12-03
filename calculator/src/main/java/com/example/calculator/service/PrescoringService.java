package com.example.calculator.service;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.calculator.dto.LoanStatementRequestDto;
import com.example.calculator.dto.ScoringDataDto;
import com.example.calculator.prescoring.PrescoringRules;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Api(tags = "Prescoring Service")
@Slf4j
@Service
public class PrescoringService {


    public void validate(LoanStatementRequestDto request) {
        log.info("Starting validation for LoanStatementRequestDto: {}", request);

        // Валидация данных. Если данные невалидны, выбрасывается исключение
        PrescoringRules.validateName(request.getFirstName());
        PrescoringRules.validateName(request.getLastName());

        // Проверка на null для middleName, если оно есть, валидируем
        if (request.getMiddleName() != null) {
            PrescoringRules.validateName(request.getMiddleName());
        }

        PrescoringRules.validateCreditAmount(request.getAmount());
        PrescoringRules.validateLoanTerm(request.getTerm());
        PrescoringRules.validateBirthDate(request.getBirthDate());
        PrescoringRules.validateEmail(request.getEmail());
        PrescoringRules.validatePassport(request.getPassportSeries(), request.getPassportNumber());

        // Если дошли до сюда, значит валидация прошла успешно
        log.info("LoanStatementRequestDto is valid.");
    }


        public void validate(ScoringDataDto scoringData) {
            log.info("Starting validation for ScoringDataDto: {}", scoringData);

            // Валидация для каждого поля
            PrescoringRules.validateName(scoringData.getFirstName());
            PrescoringRules.validateName(scoringData.getLastName());

            // Проверка на null для middleName, если оно есть, валидируем
            if (scoringData.getMiddleName() != null) {
                PrescoringRules.validateName(scoringData.getMiddleName());
            }

            PrescoringRules.validateCreditAmount(scoringData.getAmount());
            PrescoringRules.validateLoanTerm(scoringData.getTerm());
            PrescoringRules.validateBirthDate(scoringData.getBirthDate());
            PrescoringRules.validatePassport(scoringData.getPassportSeries(), scoringData.getPassportNumber());

            // Если все прошло успешно, валидация успешна
            log.info("ScoringDataDto is valid.");
    }
}