package com.example.deal.feignclient;

import com.example.deal.dto.CreditDto;
import com.example.deal.dto.LoanOfferDto;
import com.example.deal.dto.LoanStatementRequestDto;
import com.example.deal.dto.ScoringDataDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "calculator-client", url = "${calculator.base-url}")
public interface CalculatorOffersClient {

    @PostMapping("/offers")
    List<LoanOfferDto> getLoanOffers(@RequestBody LoanStatementRequestDto request);

    @PostMapping("/calc")
    CreditDto sendScoringData(@RequestBody ScoringDataDto scoringDataDto);
}
