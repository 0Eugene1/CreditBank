package com.example.deal.feignclient;

import com.example.deal.dto.CreditDto;
import com.example.deal.dto.ScoringDataDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "calculatorScoringClient", url = "${calculator.base-calc-url}")
public interface CalculatorScoringClient {

    @PostMapping
    CreditDto sendScoringData(@RequestBody ScoringDataDto scoringDataDto);
}
