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


    @ApiOperation(value = "Validate Loan Statement Request", notes = "Validates the loan statement request based on preset rules")
    public boolean validate(LoanStatementRequestDto request) {
        log.info("Starting validation for LoanStatementRequestDto: {}", request);


        boolean isValid = PrescoringRules.validateName(request.getFirstName()) &&
                PrescoringRules.validateName(request.getLastName()) &&
                (request.getMiddleName() == null || PrescoringRules.validateName(request.getMiddleName())) &&
                PrescoringRules.validateCreditAmount(request.getAmount()) &&
                PrescoringRules.validateLoanTerm(request.getTerm()) &&
                PrescoringRules.validateBirthDate(request.getBirthDate()) &&
                PrescoringRules.validateEmail(request.getEmail()) &&
                PrescoringRules.validatePassport(request.getPassportSeries(), request.getPassportNumber());

        if (isValid) {
            log.info("Validation successful for LoanStatementRequestDto: {}", request);
        } else {
            log.warn("Validation failed for LoanStatementRequestDto: {}", request);
        }
        return isValid;
    }

    @ApiOperation(value = "Validate Scoring Data", notes = "Validates the scoring data for loan application.")
    public boolean validate(ScoringDataDto scoringData) {
        log.info("Starting validation for ScoringDataDto: {}", scoringData);

        boolean isValid = PrescoringRules.validateName(scoringData.getFirstName()) &&
                PrescoringRules.validateName(scoringData.getLastName()) &&
                PrescoringRules.validateCreditAmount(scoringData.getAmount()) &&
                PrescoringRules.validateLoanTerm(scoringData.getTerm()) &&
                PrescoringRules.validateBirthDate(scoringData.getBirthDate()) &&
                PrescoringRules.validatePassport(scoringData.getPassportSeries(), scoringData.getPassportNumber());


        if (isValid) {
            log.info("Validation successful for ScoringDataDto: {}", scoringData);
        } else {
            log.warn("Validation failed for ScoringDataDto: {}", scoringData);
        }
        return isValid;
    }
}