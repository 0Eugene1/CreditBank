package com.example.deal.swagger;

import com.example.deal.dto.FinishRegistrationRequestDto;
import com.example.deal.dto.LoanOfferDto;
import com.example.deal.dto.LoanStatementRequestDto;
import com.example.deal.dto.SesCodeDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

public interface DealControllerApi {

    @Operation(summary = "Calculation of possible loan terms",
            description = "Returns a list of available offers based on customer data.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched loan offers",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoanOfferDto[].class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)))
    })
    ResponseEntity<List<LoanOfferDto>> calculateLoanOffers(@Parameter(description = "Loan statement request") LoanStatementRequestDto request);

    @Operation(summary = "Select a loan offer",
            description = "Allows you to select one of the proposed loan offers.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Loan offer selected successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid loan offer",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)))
    })
    ResponseEntity<Void> selectLoanOffer(@Parameter(description = "Selected loan offer") LoanOfferDto offer);

    @Operation(summary = "Complete registration",
            description = "Completes the registration process by application ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registration completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid registration data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)))
    })
    ResponseEntity<Void> finishRegistration(@Parameter(description = "Statement ID") String statementId,
                                            @Parameter(description = "Finish registration data") FinishRegistrationRequestDto request);

    @Operation(summary = "Send loan documents to client",
            description = "Sends the requested loan documents to the client's email address.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Documents sent successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "404", description = "Statement not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)))
    })
    @PostMapping("/{statementId}/send")
    ResponseEntity<Void> sendDocuments(@PathVariable UUID statementId);

    @Operation(summary = "Complete registration process by statement ID",
            description = "Completes the registration process by statement ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registration process completed"),
            @ApiResponse(responseCode = "400", description = "Invalid statement ID or registration data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "404", description = "Statement not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)))
    })
    ResponseEntity<Void> signDocuments(@Parameter(description = "The unique identifier of the loan statement") UUID statementId);

    @Operation(summary = "Validate code for signing loan documents",
            description = "Validates the code for signing the loan documents.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Code validated and documents signed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid code or request data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "404", description = "Statement not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)))
    })
    ResponseEntity<Void> confirmCode(@Parameter(description = "The unique identifier of the loan statement") UUID statementId,
                                     @RequestBody @Valid SesCodeDTO sesCodeDTO);
}
