package com.example.statement.service;

import com.example.statement.dto.LoanOfferDto;
import com.example.statement.dto.LoanStatementRequestDto;
import com.example.statement.prescoring.PrescoringRules;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanStatementService {

    private final DealService dealService;

    public List<LoanOfferDto> processLoanStatement(LoanStatementRequestDto requestDto) {
        log.info("Incoming LoanStatementRequestDto: {}", requestDto);

        PrescoringRules.validateBirthDate(requestDto.getBirthDate());
        PrescoringRules.validateCreditAmount(requestDto.getAmount());
        PrescoringRules.validateEmail(requestDto.getEmail());
        PrescoringRules.validateLoanTerm(requestDto.getTerm());
        PrescoringRules.validateName(requestDto.getFirstName());
        PrescoringRules.validateName(requestDto.getLastName());
        PrescoringRules.validatePassport(requestDto.getPassportSeries(),requestDto.getPassportNumber());
        log.info("Prescoring passed successfully for request: {}", requestDto);

        //Отправка в мс Deal
        List<LoanOfferDto> loanOffers = new ArrayList<>(dealService.sendDealStatement(requestDto));
        loanOffers.sort(Comparator.comparing(LoanOfferDto::getRate).reversed());
        log.info("Received loan offers: {}", loanOffers);
        return loanOffers;

    }
}
