package com.example.deal.mccalculator;

import com.example.deal.dto.CreditDto;
import com.example.deal.dto.LoanOfferDto;
import com.example.deal.dto.LoanStatementRequestDto;
import com.example.deal.dto.ScoringDataDto;
import com.example.deal.feignclient.CalculatorOffersClient;
import com.example.deal.feignclient.CalculatorScoringClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalculatorClient {

    private final CalculatorOffersClient calculatorOffersClient;
    private final CalculatorScoringClient calculatorScoringClient;

    public List<LoanOfferDto> getLoanOffers(LoanStatementRequestDto request) {
        log.info("Requesting loan offers for: {}", request);
        return calculatorOffersClient.getLoanOffers(request);
    }

    public CreditDto sendScoringData(ScoringDataDto scoringDataDto) {
        log.info("Sending scoring data: {}", scoringDataDto);
        return calculatorScoringClient.sendScoringData(scoringDataDto);
    }
}
