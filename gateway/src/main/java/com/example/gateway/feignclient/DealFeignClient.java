package com.example.gateway.feignclient;

import com.example.deal.dto.SesCodeDTO;
import com.example.deal.dto.StatementDto;
import com.example.gateway.dto.FinishRegistrationRequestDto;
import com.example.gateway.dto.LoanOfferDto;
import com.example.gateway.dto.LoanStatementRequestDto;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "deal-service", url = "${deal.base-url}")
public interface DealFeignClient {

    @PostMapping("${deal.service-select-offer}")
    void selectOffer(@RequestBody LoanOfferDto offerDto);

    @PostMapping("${deal.service-loan-offers}")
    List<LoanOfferDto> calculateLoanOffers(@RequestBody LoanStatementRequestDto loanStatementRequestDto);

    @PostMapping("${deal.service-finish-registration}")
    void finishRegistration(@PathVariable("statementId") String statementId,
                            @RequestBody FinishRegistrationRequestDto request);

    @PostMapping("${deal.service-send-documents}")
    void sendDocuments(@PathVariable("statementId") UUID statementId);

    @PostMapping("${deal.service-sign-documents}")
    void signDocuments(@PathVariable("statementId") UUID statementId);

    @PostMapping("${deal.service-confirm-code}")
    void confirmCode(@PathVariable("statementId") UUID statementId,  @RequestBody @Valid SesCodeDTO sesCodeDTO);

    @GetMapping("${deal.admin-service-get-statement-by-id}")
    StatementDto getStatementById(@PathVariable UUID statementId);

    @GetMapping("${deal.admin-service-get-all-statements}")
    List<StatementDto> fetchAllStatementsFromDealService();

}

