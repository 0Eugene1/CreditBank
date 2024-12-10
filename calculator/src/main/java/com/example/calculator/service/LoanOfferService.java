package com.example.calculator.service;

import com.example.calculator.dto.LoanOfferDto;
import com.example.calculator.dto.LoanStatementRequestDto;
import com.example.calculator.factory.LoanOfferFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanOfferService {


    private final LoanOfferFactory loanOfferFactory;
    private final PrescoringService prescoringService;

    public List<LoanOfferDto> calculateLoanOffers(LoanStatementRequestDto request) {
        log.debug("Received loan statement request: {}", request);


        int term = request.getTerm();

        // Используем фабрику для создания предложений
        List<LoanOfferDto> offers = loanOfferFactory.createOffers(request, term);

        //Сортируем предложение по ставке
        offers.sort(Comparator.comparingDouble(o -> o.getRate().doubleValue()));

        log.debug("Loan offers calculated: {}", offers);
        return offers;
    }
}
