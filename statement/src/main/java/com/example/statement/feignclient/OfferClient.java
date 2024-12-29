package com.example.statement.feignclient;

import com.example.statement.dto.LoanOfferDto;
import com.example.statement.dto.LoanStatementRequestDto;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(name = "deal-service", url = "${deal.service.base-url}")
public interface OfferClient {

    @PostMapping("${deal.service.offer-select}")
    void selectOffer(@RequestBody LoanOfferDto offerDto);

    @PostMapping("${deal.service.statement}")
    List<LoanOfferDto> calculateLoanOffers(@RequestBody LoanStatementRequestDto loanStatementRequestDto);
}

