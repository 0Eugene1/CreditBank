package com.example.calculator.swagger;

import com.example.calculator.dto.CreditDto;
import com.example.calculator.dto.LoanOfferDto;
import com.example.calculator.dto.LoanStatementRequestDto;
import com.example.calculator.dto.ScoringDataDto;
import com.example.calculator.exception.CalculatorError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface LoanCalcControllerApi {

    @Operation(summary = "Calculate Credit Details",
            description = "Calculates credit details including rate, monthly payment, PSK, and payment schedule.")

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully calculated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreditDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request, prescoring validation failed",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CalculatorError.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CalculatorError.class)))
    })
    ResponseEntity<CreditDto> calculateCredit(@Parameter(description = "Scoring data for calculating the loan rate") ScoringDataDto data);

    @Operation(summary = "Generate Loan Offers", description = "Generates loan offers based on loan statement request.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Loan offers generated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoanOfferDto[].class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CalculatorError.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CalculatorError.class)))
    })
    ResponseEntity<List<LoanOfferDto>> offers(@Parameter(description = "Loan statement request") LoanStatementRequestDto request);

}
