package com.example.calculator.controller;


import com.example.calculator.dto.CreditDto;
import com.example.calculator.dto.LoanOfferDto;
import com.example.calculator.dto.LoanStatementRequestDto;
import com.example.calculator.dto.ScoringDataDto;
import com.example.calculator.service.LoanCalcService;
import com.example.calculator.service.LoanOfferService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;


@Slf4j
@RestController
@RequestMapping("/calculator")
public class CalculatorController {

    private final LoanCalcService loanCalcService;
    private final LoanOfferService loanOfferService;

    public CalculatorController(LoanCalcService loanCalcService, LoanOfferService loanOfferService) {
        this.loanCalcService = loanCalcService;
        this.loanOfferService = loanOfferService;
    }

    @PostMapping("/offers")
    public List<LoanOfferDto> offers(@RequestBody LoanStatementRequestDto request) {
        log.info("A request has been received to calculate possible loan terms: {}", request);
        return loanOfferService.calculateLoanOffers(request);
    }

    @PostMapping("/calc")
    public CreditDto calculateCredit(@RequestBody ScoringDataDto data) {
        log.info("Request for loan calculation received: {}", data);
        return loanCalcService.calculateCredit(data);
    }
}