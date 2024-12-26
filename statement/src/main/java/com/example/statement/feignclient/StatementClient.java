package com.example.statement.feignclient;

import com.example.statement.dto.LoanOfferDto;
import com.example.statement.dto.LoanStatementRequestDto;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(name = "statement-service", url = "${deal.service.statement.base-url}")
public interface StatementClient {

    @PostMapping
    List<LoanOfferDto> calculateLoanOffers(@RequestBody LoanStatementRequestDto loanStatementRequestDto);
}
