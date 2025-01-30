package com.example.statement.swagger;

import com.example.statement.dto.LoanOfferDto;
import com.example.statement.dto.LoanStatementRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

public interface DealApi {

    @Operation(
            summary = "Выбор кредитного предложения",
            description = "Отправка выбранного предложения на МС Deal"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное выполнение"),
            @ApiResponse(responseCode = "400", description = "Некорректные входные данные"),
            @ApiResponse(responseCode = "404", description = "Предложение с указанным statementId не найдено"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PostMapping("/offer")
    ResponseEntity<Void> selectOneOffer(@RequestBody LoanOfferDto loanOfferDto);

    @Operation(
            summary = "Прескоринг и расчёт возможных условий кредита",
            description = "Выполняет прескоринг и отправляет запрос на расчёт условий кредита."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный расчёт условий кредита"),
            @ApiResponse(responseCode = "400", description = "Некорректные входные данные"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PostMapping("/statement")
    ResponseEntity<List<LoanOfferDto>> prescoringRequest(@RequestBody LoanStatementRequestDto request);
}


