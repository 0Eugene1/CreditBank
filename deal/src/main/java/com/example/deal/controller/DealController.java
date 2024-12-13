package com.example.deal.controller;

import com.example.deal.dto.FinishRegistrationRequestDto;
import com.example.deal.dto.LoanOfferDto;
import com.example.deal.dto.LoanStatementRequestDto;
import com.example.deal.exception.GlobalExceptionHandler;
import com.example.deal.service.FinishRegRequestService;
import com.example.deal.service.LoanOfferService;
import com.example.deal.service.SelectOffersService;
import com.example.deal.swagger.DealControllerApi;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/deal")
public class DealController implements DealControllerApi {

    private final LoanOfferService loanOfferService;
    private final SelectOffersService selectOffersService;
    private final FinishRegRequestService finishRegRequestService;


    @PostMapping("/statement")
    public ResponseEntity<List<LoanOfferDto>> calculateLoanOffers(@Valid @RequestBody LoanStatementRequestDto request) {
        log.info("Received loan statement request: {}", request);

        List<LoanOfferDto> loanOffers = loanOfferService.createClientFromRequest(request);
        return ResponseEntity.ok(loanOffers);
    }

    @PostMapping("/offer/select")
    public ResponseEntity<Void> selectLoanOffer(@Valid @RequestBody LoanOfferDto offer) {
        log.info("Received loan offer selection request: {}", offer);

        selectOffersService.selectLoanOffer(offer);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/calculate/{statementId}")
    public ResponseEntity<Void> finishRegistration(@PathVariable String statementId, @Valid
                                                   @RequestBody FinishRegistrationRequestDto request)   {
        log.info("Received finish registration request for statementId: {}, with data: {}", statementId, request);

        finishRegRequestService.finishRegistration(statementId, request);
        return ResponseEntity.ok().build();
    }
    }

