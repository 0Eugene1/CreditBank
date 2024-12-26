package com.example.deal.feignclient;

import com.example.deal.dto.LoanOfferDto;
import com.example.deal.dto.LoanStatementRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "calculatorOffersClient", url = "${calculator.base-offers-url}")
public interface CalculatorOffersClient {

    @PostMapping
    List<LoanOfferDto> getLoanOffers(@RequestBody LoanStatementRequestDto request);
}

