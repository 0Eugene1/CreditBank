package com.example.calculator.service;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.calculator.dto.LoanStatementRequestDto;
import com.example.calculator.dto.ScoringDataDto;
import com.example.calculator.prescoring.PrescoringRules;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Api(tags = "Prescoring Service")
@Service
public class PrescoringService {


    private static final Logger logger = LoggerFactory.getLogger(PrescoringService.class);

    @ApiOperation(value = "Validate Loan Statement Request", notes = "Validates the loan statement request based on preset rules")
    public boolean validate(LoanStatementRequestDto request) {
        logger.info("Starting validation for LoanStatementRequestDto: {}", request);


        boolean isValid = PrescoringRules.validateName(request.getFirstName()) &&
                PrescoringRules.validateName(request.getLastName()) &&
                (request.getMiddleName() == null || PrescoringRules.validateName(request.getMiddleName())) &&
                PrescoringRules.validateCreditAmount(request.getAmount()) &&
                PrescoringRules.validateLoanTerm(request.getTerm()) &&
                PrescoringRules.validateBirthDate(request.getBirthDate()) &&
                PrescoringRules.validateEmail(request.getEmail()) &&
                PrescoringRules.validatePassport(request.getPassportSeries(), request.getPassportNumber());

        if (isValid){
            logger.info("Validation successful for LoanStatementRequestDto: {}", request);
        } else {
            logger.warn("Validation failed for LoanStatementRequestDto: {}", request);
        }
        return isValid;
    }

    @ApiOperation(value = "Validate Scoring Data", notes = "Validates the scoring data for loan application.")
    public boolean validate(ScoringDataDto scoringData) {
            logger.info("Starting validation for ScoringDataDto: {}", scoringData);

            boolean isValid = PrescoringRules.validateName(scoringData.getFirstName()) &&
                    PrescoringRules.validateName(scoringData.getLastName()) &&
                    PrescoringRules.validateCreditAmount(scoringData.getAmount()) &&
                    PrescoringRules.validateLoanTerm(scoringData.getTerm()) &&
                    PrescoringRules.validateBirthDate(scoringData.getBirthDate()) &&
                    PrescoringRules.validatePassport(scoringData.getPassportSeries(), scoringData.getPassportNumber());


        if (isValid) {
            logger.info("Validation successful for ScoringDataDto: {}", scoringData);
        } else {
            logger.warn("Validation failed for ScoringDataDto: {}", scoringData);
        }
        return isValid;
    }

    //    public boolean validate(ScoringDataDto scoringData) {
//        System.out.println("Starting validation for: " + scoringData);
//
//        boolean isNameValid = PrescoringRules.validateName(scoringData.getFirstName()) &&
//                PrescoringRules.validateName(scoringData.getLastName());
//        System.out.println("Name validation result: " + isNameValid);
//
//        boolean isAmountValid = PrescoringRules.validateCreditAmount(scoringData.getAmount());
//        System.out.println("Amount validation result: " + isAmountValid);
//
//        boolean isTermValid = PrescoringRules.validateLoanTerm(scoringData.getTerm());
//        System.out.println("Loan term validation result: " + isTermValid);
//
//        boolean isBirthDateValid = PrescoringRules.validateBirthDate(scoringData.getBirthDate());
//        System.out.println("Birth date validation result: " + isBirthDateValid);
//
//        boolean isPassportValid = PrescoringRules.validatePassport(scoringData.getPassportSeries(), scoringData.getPassportNumber());
//        System.out.println("Passport validation result: " + isPassportValid);
//
//        return isNameValid && isAmountValid && isTermValid && isBirthDateValid && isPassportValid;
//    }

}