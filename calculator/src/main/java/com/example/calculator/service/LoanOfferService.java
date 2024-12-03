package com.example.calculator.service;

import com.example.calculator.dto.LoanOfferDto;
import com.example.calculator.dto.LoanStatementRequestDto;
import com.example.calculator.factory.LoanOfferFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class LoanOfferService {


    private final LoanOfferFactory loanOfferFactory;
    private final PrescoringService prescoringService;

    public List<LoanOfferDto> calculateLoanOffers(LoanStatementRequestDto request) {
        log.debug("Received loan statement request: {}", request);


        int term = request.getTerm();
        log.debug("Loan term: {}", term);

        // Используем фабрику для создания предложений
        List<LoanOfferDto> offers = loanOfferFactory.createOffers(request, term);

        //Сортируем предложение по ставке
        log.debug("Sorting loan offers by rate");
        offers.sort(Comparator.comparingDouble(o -> o.getRate().doubleValue()));

        log.debug("Loan offers calculated: {}", offers);
        return offers;
    }
}
