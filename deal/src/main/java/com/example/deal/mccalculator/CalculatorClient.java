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

    @Value("${calculator.base-offers-url}")
    private String calculatorBaseOffersUrl;

    @Value("${calculator.base-calc-url}")
    private String calculatorBaseCalcUrl;

    // Конструктор для настройки RestTemplate через RestTemplateBuilder
    public CalculatorClient(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(5)) // Устанавливаем таймаут на соединение
                .setReadTimeout(Duration.ofSeconds(10)) // Таймаут на чтение данных
                .build();
    }

//    // Метод для получения предложения по кредитам
//    public List<LoanOfferDto> getLoanOffers(LoanStatementRequestDto request) {
//        log.info("Sending loan calculation request to calculator: {}", request);
//
//        log.debug("Request body: {}", request);
//        ResponseEntity<List<LoanOfferDto>> response = restTemplate.exchange(
//                calculatorBaseOffersUrl,
//                HttpMethod.POST,
//                new HttpEntity<>(request),
//                new ParameterizedTypeReference<>() {}
//        );
//        log.debug("Response body: {}", response.getBody());
//
//        validateResponseStatus(response.getStatusCode());
//        log.info("Received {} loan offers from calculator.", response.getBody().size());
//        return response.getBody();
//    }

//    private void validateResponseStatus(HttpStatus status) {
//        if (!status.is2xxSuccessful()) {
//            throw new RestClientException("Unexpected response status: " + status);
//        }
//    }
public List<LoanOfferDto> getLoanOffers(LoanStatementRequestDto request) {
    log.info("Sending loan calculation request to calculator: {}", request);

    // Логируем отправляемый запрос
    try {
        log.debug("Request body: {}", new ObjectMapper().writeValueAsString(request));
    } catch (JsonProcessingException e) {
        log.error("Failed to serialize request body for logging", e);
    }

    ResponseEntity<List<LoanOfferDto>> response = restTemplate.exchange(
            calculatorBaseOffersUrl,
            HttpMethod.POST,
            new HttpEntity<>(request),
            new ParameterizedTypeReference<List<LoanOfferDto>>() {}
    );
    log.debug("Response body: {}", response.getBody());

    if (!response.getStatusCode().is2xxSuccessful()) {
        throw new RestClientException("Unexpected response status: " + response.getStatusCode());
    }

    log.info("Received {} loan offers from calculator.", response.getBody().size());

    return response.getBody();
    }

    // Метод для отправки данных скоринга
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


        validateResponseStatus(response.getStatusCode());
        log.info("Received credit data from calculator service: {}", response.getBody());
        return response.getBody();
    }

    private void validateResponseStatus(HttpStatusCode statusCode) {
        // Преобразуем HttpStatusCode в HttpStatus
        HttpStatus status = HttpStatus.resolve(statusCode.value());

        if (status == null || !status.is2xxSuccessful()) {
            log.error("Calculator service returned error status: {}", status);
            throw new IllegalStateException("Error response from calculator service: " + status);
        }
    }
}
