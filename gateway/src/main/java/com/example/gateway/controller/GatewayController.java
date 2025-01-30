package com.example.gateway.controller;

import com.example.deal.dto.SesCodeDTO;
import com.example.deal.dto.StatementDto;
import com.example.gateway.dto.FinishRegistrationRequestDto;
import com.example.gateway.dto.LoanOfferDto;
import com.example.gateway.dto.LoanStatementRequestDto;
import com.example.gateway.service.DealGatewayService;
import com.example.gateway.swagger.DealGatewayApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/gateway")
@RequiredArgsConstructor
@Slf4j
public class GatewayController implements DealGatewayApi {

    private final DealGatewayService dealGatewayService;

    @Override
    @PostMapping("/statement")
    public ResponseEntity<List<LoanOfferDto>> calculateLoanOffers(@RequestBody LoanStatementRequestDto loanStatementRequestDto) {
        log.info("Received request to calculate loan offers: {}", loanStatementRequestDto);
        List<LoanOfferDto> response = dealGatewayService.sendDealStatement(loanStatementRequestDto);
        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping("/offer/select")
    public ResponseEntity<Void> selectOffer(@RequestBody LoanOfferDto offerDto) {
        log.info("Received request to select loan offer: {}", offerDto);
        dealGatewayService.selectOffer(offerDto);
        return ResponseEntity.ok().build();
    }

    @Override
    @PostMapping("/calculate/{statementId}")
    public ResponseEntity<Void> finishRegistration(@PathVariable String statementId,
                                                   @RequestBody FinishRegistrationRequestDto request) {
        log.info("Received finish registration request via Gateway for statementId: {}, with data: {}", statementId, request);
        dealGatewayService.finishRegistration(statementId, request);
        return ResponseEntity.ok().build();
    }

    @Override
    @PostMapping("/document/{statementId}/send")
    public ResponseEntity<Void> sendDocuments(@PathVariable UUID statementId) {
        log.info("Received send documents request via Gateway for statementId: {}", statementId);
        dealGatewayService.sendDocuments(statementId);
        return ResponseEntity.ok().build();
    }

    @Override
    @PostMapping("/document/{statementId}/sign")
    public ResponseEntity<Void> signDocuments(@PathVariable UUID statementId) {
        log.info("Received sign documents request via Gateway for statementId: {}", statementId);
        dealGatewayService.signDocuments(statementId);
        return ResponseEntity.ok().build();
    }

    @Override
    @PostMapping("/document/{statementId}/code")
    public ResponseEntity<Void> confirmCode(@PathVariable UUID statementId,
                                            @RequestBody SesCodeDTO sesCodeDTO) {
        log.info("Received confirm code request via Gateway for statementId: {}", statementId);
        dealGatewayService.confirmCode(statementId, sesCodeDTO);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/deal/admin/statement/{statementId}")
    public ResponseEntity<StatementDto> getStatementById(@PathVariable UUID statementId) {
        StatementDto statementDto = dealGatewayService.getStatementById(statementId);
        return ResponseEntity.ok(statementDto);
    }


    @GetMapping("/admin/statement")
    public ResponseEntity<List<StatementDto>> getStatements() {
        List<StatementDto> statements = dealGatewayService.getAllStatements();
        return ResponseEntity.ok(statements);
    }
}
