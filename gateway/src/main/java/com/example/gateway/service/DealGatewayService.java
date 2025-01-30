package com.example.gateway.service;

import com.example.deal.dto.SesCodeDTO;
import com.example.deal.dto.StatementDto;
import com.example.gateway.dto.FinishRegistrationRequestDto;
import com.example.gateway.dto.LoanOfferDto;
import com.example.gateway.dto.LoanStatementRequestDto;
import com.example.gateway.feignclient.DealFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DealGatewayService {

    private final DealFeignClient dealClient;

    public List<LoanOfferDto> sendDealStatement(LoanStatementRequestDto loanStatementRequestDto) {
        List<LoanOfferDto> response = dealClient.calculateLoanOffers(loanStatementRequestDto);
        log.info("Received loan offers: {}", response);
        return response;
    }

    public void selectOffer(LoanOfferDto offerDto) {
        dealClient.selectOffer(offerDto);
        log.info("Loan offer selected successfully.");
    }

    public void finishRegistration(String statementId, FinishRegistrationRequestDto request) {
        dealClient.finishRegistration(statementId, request);
        log.info("Gateway: Registration finished for statementId: {}", statementId);
    }

    public void sendDocuments(UUID statementId) {
        dealClient.sendDocuments(statementId);
        log.info("Gateway: Documents sent for statementId: {}", statementId);
    }

    public void signDocuments(UUID statementId) {
        dealClient.signDocuments(statementId);
        log.info("Gateway: Documents signed for statementId: {}", statementId);
    }

    public void confirmCode(UUID statementId, SesCodeDTO sesCodeDto) {
        dealClient.confirmCode(statementId, sesCodeDto);
        log.info("Gateway: Code confirmed for statementId: {}", statementId);
    }

    // Получение заявки по ID
    public StatementDto getStatementById(UUID statementId) {
        return dealClient.getStatementById(statementId);
    }
    // Получение всех заявок
    public List<StatementDto> getAllStatements() {
       return dealClient.fetchAllStatementsFromDealService();
    }
}