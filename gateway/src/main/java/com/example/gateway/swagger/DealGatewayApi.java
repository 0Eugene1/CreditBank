package com.example.gateway.swagger;

import com.example.deal.dto.SesCodeDTO;
import com.example.deal.dto.StatementDto;
import com.example.gateway.dto.FinishRegistrationRequestDto;
import com.example.gateway.dto.LoanOfferDto;
import com.example.gateway.dto.LoanStatementRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@Tag(name = "Gateway API", description = "API для работы с микросервисом Deal через Gateway")
public interface DealGatewayApi {

    @Operation(summary = "Calculate loan offers", description = "Send loan statement to calculate loan offers.")
    @PostMapping("/gateway/deal/statement")
    ResponseEntity<List<LoanOfferDto>> calculateLoanOffers(@RequestBody LoanStatementRequestDto loanStatementRequestDto);

    @Operation(summary = "Select loan offer", description = "Select a loan offer from the available options.")
    @PostMapping("/gateway/deal/offer/select")
    ResponseEntity<Void> selectOffer(@RequestBody LoanOfferDto offerDto);

    @Operation(summary = "Завершить регистрацию",
            description = "Передача данных для завершения регистрации через Gateway.")
    @PostMapping("/gateway/deal/calculate/{statementId}")
    ResponseEntity<Void> finishRegistration(@PathVariable String statementId,
                                            @RequestBody @Valid FinishRegistrationRequestDto request);

    @Operation(summary = "Отправить документы",
            description = "Отправка документов через Gateway.")
    @PostMapping("/gateway/deal/document/{statementId}/send")
    ResponseEntity<Void> sendDocuments(@PathVariable UUID statementId);

    @Operation(summary = "Подписать документы",
            description = "Подписание документов через Gateway.")
    @PostMapping("/gateway/deal/document/{statementId}/sign")
    ResponseEntity<Void> signDocuments(@PathVariable UUID statementId);

    @Operation(summary = "Подтвердить код",
            description = "Подтверждение подписания документов кодом через Gateway.")
    @PostMapping("/gateway/deal/document/{statementId}/code")
    ResponseEntity<Void> confirmCode(@PathVariable UUID statementId,  @RequestBody @Valid SesCodeDTO sesCodeDTO);

    @Operation(summary = "Получить заявку по ID", description = "Получить заявку по ID для администрирования.")
    @GetMapping("/gateway/deal/admin/statement/{statementId}")
    ResponseEntity<StatementDto> getStatementById(@PathVariable UUID statementId);

    @Operation(summary = "Получить все заявки", description = "Получить все заявки для администрирования.")
    @GetMapping("/gateway/deal/admin/statement")
    ResponseEntity<List<StatementDto>> getStatements();
}




