package com.example.calculator.controller;


import com.example.calculator.dto.CreditDto;
import com.example.calculator.dto.LoanOfferDto;
import com.example.calculator.dto.LoanStatementRequestDto;
import com.example.calculator.dto.ScoringDataDto;
import com.example.calculator.service.LoanCalcService;
import com.example.calculator.service.LoanOfferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.Collections;
import java.util.List;



@RestController
@RequestMapping("/calculator")
public class CalculatorController {

    private final LoanCalcService loanCalcService;
    private final LoanOfferService loanOfferService;
    private static final Logger logger = LoggerFactory.getLogger(CalculatorController.class);

    public CalculatorController(LoanCalcService loanCalcService, LoanOfferService loanOfferService) {
        this.loanCalcService = loanCalcService;
        this.loanOfferService = loanOfferService;
    }

    @PostMapping("/offers")
    public List<LoanOfferDto> offers(@RequestBody LoanStatementRequestDto request) {
        logger.info("A request has been received to calculate possible loan terms: {}", request);
        try {
            return loanOfferService.calculateLoanOffers(request);
        } catch (IllegalArgumentException e) {
            logger.error("Error when calculating loan terms: {}", e.getMessage(), e); //FIXME LOMBOCK logger
            return Collections.emptyList(); // FIXME лучше вернуть ошибку
        }
    }

    @PostMapping("/calc")
    public CreditDto calculateCredit(@RequestBody ScoringDataDto data) {
        logger.info("Request for loan calculation received: {}", data);
        try {
            return loanCalcService.calculateCredit(data);
        } catch (IllegalArgumentException e) {
            logger.debug("Error when calculating credit: {}", e.getMessage(), e);
            return new CreditDto();
        }
    }
}