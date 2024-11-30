package com.example.calculator.service;

import com.example.calculator.dto.LoanOfferDto;
import com.example.calculator.dto.LoanStatementRequestDto;
import com.example.calculator.factory.LoanOfferFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class LoanOfferService {


    private final LoanOfferFactory loanOfferFactory;
    private final PrescoringService prescoringService;


    public LoanOfferService(LoanOfferFactory loanOfferFactory, PrescoringService prescoringService) {
        this.loanOfferFactory = loanOfferFactory;
        this.prescoringService = prescoringService;
    }

    @Operation(summary = "Calculate loan offers", description = "Calculate different loan offers based on the request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully calculated loan offers"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public List<LoanOfferDto> calculateLoanOffers(LoanStatementRequestDto request) {
        log.debug("Received loan statement request: {}", request);

        if (!prescoringService.validate(request)) {
            log.warn("Prescoring failed for request: {}", request);
            throw new IllegalArgumentException("Предварительная проверка заявки не пройдена");
        }

        List<LoanOfferDto> offers = new ArrayList<>();
        int term = request.getTerm();
        log.debug("Loan term: {}", term);

        // Используем фабрику для создания предложений
        try {
            log.info("Creating loan offers without insurance, non-salary client");
            LoanOfferDto offer1 = loanOfferFactory.createOffer(request, term, false, false);
            if (offer1 != null) offers.add(offer1);

            log.info("Creating loan offers with insurance, non-salary client");
            LoanOfferDto offer2 = loanOfferFactory.createOffer(request, term, true, false);
            if (offer2 != null) offers.add(offer2);

            log.info("Creating loan offers with insurance, salary client");
            LoanOfferDto offer3 = loanOfferFactory.createOffer(request, term, true, true);
            if (offer3 != null) offers.add(offer3);

            log.info("Creating loan offers without insurance, salary client");
            LoanOfferDto offer4 = loanOfferFactory.createOffer(request, term, false, true);
            if (offer4 != null) offers.add(offer4);
        } catch (Exception e) {
            log.error("Error while creating loan offers: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create loan offers", e);
        }

        offers.removeIf(offer -> offer.getRate() == null);
        offers.removeIf(Objects::isNull);

        log.debug("Sorting loan offers by rate");
        offers.sort(Comparator.comparingDouble(o -> o.getRate().doubleValue()));

        log.debug("Loan offers calculated: {}", offers);
        return offers;
    }
}
