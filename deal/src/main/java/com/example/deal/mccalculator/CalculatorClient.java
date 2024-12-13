package com.example.deal.mccalculator;

import com.example.deal.dto.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.List;

@Component
@Slf4j
public class CalculatorClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${calculator.base-offers-url}")
    private String calculatorBaseOffersUrl;

    @Value("${calculator.base-calc-url}")
    private String calculatorBaseCalcUrl;

    public CalculatorClient(RestTemplateBuilder restTemplateBuilder, ObjectMapper objectMapper) {
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = objectMapper;
    }

    public List<LoanOfferDto> getLoanOffers(LoanStatementRequestDto request) {
        log.info("Sending loan calculation request to calculator: {}", request);

        try {
            log.debug("Request body: {}", objectMapper.writeValueAsString(request));
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize request body for logging", e);
            throw new RestClientException("Error serializing request body", e);
        }

        ResponseEntity<List<LoanOfferDto>> response;
        try {
            response = restTemplate.exchange(
                    calculatorBaseOffersUrl,
                    HttpMethod.POST,
                    new HttpEntity<>(request),
                    new ParameterizedTypeReference<List<LoanOfferDto>>() {}
            );
        } catch (RestClientException e) {
            log.error("Error calling loan calculator service", e);
            throw new RestClientException("Error calling loan calculator service", e);
        }

        if (!response.getStatusCode().is2xxSuccessful()) {
            logErrorResponse(response);
            throw new RestClientException("Unexpected response status: " + response.getStatusCode());
        }

        log.info("Received {} loan offers from calculator.", response.getBody().size());
        return response.getBody();
    }

    public CreditDto sendScoringData(ScoringDataDto scoringDataDto) {
        log.info("Sending scoring data to calculator service: {}", scoringDataDto);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ScoringDataDto> request = new HttpEntity<>(scoringDataDto, headers);

        ResponseEntity<CreditDto> response = restTemplate.exchange(
                calculatorBaseCalcUrl,
                HttpMethod.POST,
                request,
                CreditDto.class
        );

        handleScoringDataResponse(response);
        return response.getBody();
    }

    private void handleScoringDataResponse(ResponseEntity<CreditDto> response) {
        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Scoring service returned error status: {}", response.getStatusCode());
            throw new RestClientException("Error response from scoring service: " + response.getStatusCode());
        }
        CreditDto body = response.getBody();
        if (body == null) {
            log.error("Scoring service returned empty body");
            throw new RestClientException("Empty body from scoring service");
        }
        log.info("Received credit data from calculator service: {}", body);
    }

    private void logErrorResponse(ResponseEntity<?> response) {
        log.error("Received error response: Status: {}, Body: {}", response.getStatusCode(), response.getBody());
    }
}
