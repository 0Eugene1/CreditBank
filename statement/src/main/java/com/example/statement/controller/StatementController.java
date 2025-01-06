package com.example.statement.controller;

import com.example.statement.dto.LoanOfferDto;
import com.example.statement.dto.LoanStatementRequestDto;
import com.example.statement.service.DealService;
import com.example.statement.service.LoanStatementService;
import com.example.statement.swagger.DealApi;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/statement")
@RequiredArgsConstructor
public class StatementController implements DealApi {

    private final DealService dealService;
    private final LoanStatementService loanStatementService;

    @Override
    @PostMapping("/statement")
    public ResponseEntity<List<LoanOfferDto>> prescoringRequest(@Valid @RequestBody LoanStatementRequestDto request) {
        List<LoanOfferDto> loanOffer = loanStatementService.processLoanStatement(request);
        return ResponseEntity.ok(loanOffer);
    }

    @Override
    @PostMapping("/offer")
    public ResponseEntity<Void> selectOneOffer(@Valid @RequestBody LoanOfferDto loanOffer) {
        dealService.selectOffer(loanOffer);
        return ResponseEntity.ok().build();
    }

}
