package com.example.calculator.controller;


import com.example.calculator.dto.CreditDto;
import com.example.calculator.dto.LoanOfferDto;
import com.example.calculator.dto.LoanStatementRequestDto;
import com.example.calculator.dto.ScoringDataDto;
import com.example.calculator.service.LoanCalcService;
import com.example.calculator.service.LoanOfferService;
import com.example.calculator.swagger.LoanCalcControllerApi;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/calculator")
@RequiredArgsConstructor
public class CalculatorController implements LoanCalcControllerApi {

    private final LoanCalcService loanCalcService;
    private final LoanOfferService loanOfferService;


    @PostMapping("/offers")
    public ResponseEntity<List<LoanOfferDto>> offers(@Valid @RequestBody LoanStatementRequestDto request) {
        log.debug("A request has been received to calculate possible loan terms: {}", request);
        return ResponseEntity.ok(loanOfferService.calculateLoanOffers(request));
    }

    @PostMapping("/calc")
    public ResponseEntity<CreditDto> calculateCredit(@Valid @RequestBody ScoringDataDto data) {
        log.debug("Request for loan calculation received: {}", data);
        return ResponseEntity.ok(loanCalcService.calculateCredit(data));
    }
}