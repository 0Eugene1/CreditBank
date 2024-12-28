package com.example.statement.service;

import com.example.statement.dto.LoanOfferDto;
import com.example.statement.dto.LoanStatementRequestDto;
import com.example.statement.feignclient.OfferClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DealService {

    private final OfferClient offerClient;

    public List<LoanOfferDto> sendDealStatement(LoanStatementRequestDto loanStatementRequestDto) {
        return offerClient.calculateLoanOffers(loanStatementRequestDto);
    }

    public void selectOffer(LoanOfferDto offerDto) {
        offerClient.selectOffer(offerDto);
    }
}
