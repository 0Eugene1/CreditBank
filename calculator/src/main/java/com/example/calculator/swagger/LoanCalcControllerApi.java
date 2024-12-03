package com.example.calculator.swagger;

import com.example.calculator.dto.CreditDto;
import com.example.calculator.dto.LoanOfferDto;
import com.example.calculator.dto.LoanStatementRequestDto;
import com.example.calculator.dto.ScoringDataDto;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface LoanCalcControllerApi {

    @Operation(summary = "Calculate Credit Details", description = "Calculates credit details including rate, monthly payment, PSK, and payment schedule.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully calculated"),
            @ApiResponse(responseCode = "400", description = "Bad request, prescoring validation failed"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<CreditDto> calculateCredits(@Parameter(description = "Scoring data for calculating the loan rate") ScoringDataDto data);

    @Operation(summary = "Generate Loan Offers", description = "Generates loan offers based on loan statement request.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Loan offers generated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<List<LoanOfferDto>> generateLoanOffers(@Parameter(description = "Loan statement request") LoanStatementRequestDto request);

    @ApiOperation(value = "Validate Scoring Data", notes = "Validates the scoring data for loan application.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Validation successful"),
            @ApiResponse(responseCode = "400", description = "Validation failed")
    })
    void validateScoringData(@RequestBody @Valid ScoringDataDto scoringData);

    @PostMapping("/validateLoanStatementRequest")
    @ApiOperation(value = "Validate Loan Statement Request", notes = "Validates the loan statement request based on preset rules")
    void validateLoanStatementRequest(@RequestBody LoanStatementRequestDto request);

    @PostMapping("/calculateLoanRate")
    @Operation(summary = "Calculate the loan rate based on scoring data",
            description = "Calculates the modified loan rate based on various conditions like age, experience, loan amount, etc.")
    double calculateLoanRate(@RequestBody ScoringDataDto scoringData);
}
