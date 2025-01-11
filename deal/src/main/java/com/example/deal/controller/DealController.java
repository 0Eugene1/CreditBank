package com.example.deal.controller;

import com.example.deal.dto.FinishRegistrationRequestDto;
import com.example.deal.dto.LoanOfferDto;
import com.example.deal.dto.LoanStatementRequestDto;
import com.example.deal.service.*;
import com.example.deal.swagger.DealControllerApi;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/deal")
public class DealController implements DealControllerApi {

    private final LoanOfferService loanOfferService;
    private final SelectOfferService selectOffersService;
    private final FinishRegRequestService finishRegRequestService;
    private final DocumentService documentService;
    private final OfferService offerService;
    private final SesCodeService sesCodeService;


    @Override
    @PostMapping("/statement")
    public ResponseEntity<List<LoanOfferDto>> calculateLoanOffers(@Valid @RequestBody LoanStatementRequestDto request) {
        log.info("Received loan statement request: {}", request);

        List<LoanOfferDto> loanOffers = loanOfferService.createClientFromRequest(request);
        return ResponseEntity.ok(loanOffers);
    }

    @Override
    @PostMapping("/offer/select")
    public ResponseEntity<Void> selectLoanOffer(@Valid @RequestBody LoanOfferDto offer) {
        log.info("Received loan offer selection request: {}", offer);

        selectOffersService.selectLoanOffer(offer);
        return ResponseEntity.ok().build();
    }

    @Override
    @PostMapping("/calculate/{statementId}")
    public ResponseEntity<Void> finishRegistration(@PathVariable String statementId, @Valid
                                                   @RequestBody FinishRegistrationRequestDto request)   {
        log.info("Received finish registration request for statementId: {}, with data: {}", statementId, request);

        finishRegRequestService.finishRegistration(statementId, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/document/{statementId}/send")
    public ResponseEntity<Void> sendDocuments(@PathVariable UUID statementId, @RequestBody String sesCode) {
        log.info("Запрос на отправку документов для statementId: {}", statementId);
        // Проверка кода через сервис
        if (!sesCodeService.validateSesCode(statementId, sesCode)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();  // Неверный или устаревший код
        }
        return ResponseEntity.ok().build();
    }

    @Override
    @PostMapping("/document/{statementId}/sign")
    public ResponseEntity<Void> signDocuments(@PathVariable UUID statementId) {
        log.info("Запрос на подписание документов для statementId: {}", statementId);
        documentService.signDocuments(statementId);
        return ResponseEntity.ok().build();
    }

    @Override
    @PostMapping("/document/{statementId}/code")
    public ResponseEntity<Void> confirmCode(@PathVariable UUID statementId) {
        log.info("Подписание документов кодом для statementId: {}", statementId);
        documentService.confirmCode(statementId);
        return ResponseEntity.ok().build();
    }
}



